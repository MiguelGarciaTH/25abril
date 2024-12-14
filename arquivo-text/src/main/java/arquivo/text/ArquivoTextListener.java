package arquivo.text;

import arquivo.model.Article;
import arquivo.model.SearchEntity;
import arquivo.model.TextRecord;
import arquivo.repository.ArticleRepository;
import arquivo.repository.SearchEntityRepository;
import arquivo.services.ContextualTextScoreService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.*;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@EnableKafka
public class ArquivoTextListener {

    private static final Logger LOG = LoggerFactory.getLogger(ArquivoTextListener.class);

    private final ObjectMapper objectMapper;
    private final ArticleRepository articleRepository;
    private final Content systemInstruction;
    private final GenerationConfig generationConfig;
    private final List<SafetySetting> safetySettings;
    private final ContextualTextScoreService contextualTextScoreService = ContextualTextScoreService.getInstance();
    private final SearchEntityRepository searchEntityRepository;

    private int receivedCounter = 0;
    private int summaryNullCounter = 0;
    private int vertexApiErrorCounter = 0;
    private int summaryAlreadySetCounter = 0;
    private int newSummaryCounter = 0;

    ArquivoTextListener(ObjectMapper objectMapper, ArticleRepository articleRepository, SearchEntityRepository searchEntityRepository) {
        this.objectMapper = objectMapper;
        objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        this.articleRepository = articleRepository;
        this.searchEntityRepository = searchEntityRepository;

        generationConfig =
                GenerationConfig.newBuilder()
                        .setMaxOutputTokens(1024)
                        .setTemperature(0.2F)
                        .setTopP(0.95F)
                        .setResponseMimeType("application/json")
                        .build();
        safetySettings = Arrays.asList(
                SafetySetting.newBuilder()
                        .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                        .setThreshold(SafetySetting.HarmBlockThreshold.OFF)
                        .build(),
                SafetySetting.newBuilder()
                        .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                        .setThreshold(SafetySetting.HarmBlockThreshold.OFF)
                        .build(),
                SafetySetting.newBuilder()
                        .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
                        .setThreshold(SafetySetting.HarmBlockThreshold.OFF)
                        .build(),
                SafetySetting.newBuilder()
                        .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
                        .setThreshold(SafetySetting.HarmBlockThreshold.OFF)
                        .build()
        );
        systemInstruction = ContentMaker.fromMultiModalData("* Translate to Portuguese from Portugal, not from Portuguese from Brazil\n* Just focus on the summary\n* Keep the summary between 500 and 1000 words");
    }

    @KafkaListener(topics = {"${text.topic}"}, containerFactory = "kafkaListenerContainerFactory")
    public void listener(ConsumerRecord<String, String> record, Acknowledgment ack) {

        LOG.debug("Received on topic {} record {}", record.topic(), record.value());
        receivedCounter++;

        final TextRecord event;
        try {
            event = objectMapper.readValue(record.value(), TextRecord.class);
        } catch (JsonProcessingException e) {
            LOG.error("Error parsing json record: {}", record.value());
            ack.acknowledge();
            return;
        }

        final Article article = articleRepository.findById(event.articleId()).orElse(null);
        if (article == null) {
            LOG.warn("Should not happen article: {} is null", event.articleId());
            ack.acknowledge();
            return;
        }

        if (article.getSummary() != null && !article.getSummary().isBlank()) {
            LOG.warn("Summary already set, we will skip: {}", event.articleId());
            ack.acknowledge();
            summaryAlreadySetCounter++;
            return;
        }

        try {
            String summary = summarize(event.text());
            if (summary == null) {
                LOG.error("Vertex returned a null summary: {}", event.articleId());
                ack.acknowledge();
                summaryNullCounter++;
                return;
            }

            // recalculates the score over text summary, stores is greater than 5
            final SearchEntity searchEntity = searchEntityRepository.findById(event.searchEntityId()).orElse(null);
            final ContextualTextScoreService.Score score = contextualTextScoreService.scoreSummary(article.getTitle(), article.getUrl(), summary, searchEntity);

            article.setSummary(summary);
            article.setSummaryScore(score.total());
            final JsonNode scoreJson = objectMapper.convertValue(score.keywordCounter(), JsonNode.class);
            article.setSummaryScoreDetails(scoreJson);
            articleRepository.save(article);
            newSummaryCounter++;


        } catch (IOException e) {
            LOG.error("Error using Vertex API: {}", event.articleId(), e);
            ack.acknowledge();
            vertexApiErrorCounter++;
            return;
        }

        ack.acknowledge();

        logStats();
    }

    private void logStats() {
        LOG.info("Stats:");
        LOG.info("Texts received: {}", receivedCounter);
        LOG.info("Texts new summary : {}/{}", newSummaryCounter, receivedCounter);
        if (summaryAlreadySetCounter > 0) {
            LOG.warn("Texts already set summary : {}/{}", summaryAlreadySetCounter, receivedCounter);
        }
        if (vertexApiErrorCounter > 0) {
            LOG.warn("Texts api error : {}/{}", vertexApiErrorCounter, receivedCounter);
        }
        if (summaryNullCounter > 0) {
            LOG.warn("Texts summary returned null : {}/{}", summaryNullCounter, receivedCounter);
        }
    }

    private String summarize(String text) throws IOException {
        try (VertexAI vertexAi = new VertexAI("sincere-hearth-442713-p7", "europe-west2")) {
            GenerativeModel model = new GenerativeModel.Builder()
                    .setModelName("gemini-1.5-flash-002")
                    .setVertexAi(vertexAi)
                    .setGenerationConfig(generationConfig)
                    .setSafetySettings(safetySettings)
                    .setSystemInstruction(systemInstruction)
                    .build();
            var content = ContentMaker.fromMultiModalData(text);

            GenerateContentResponse generatedContentResponse = model.generateContent(content);
            if (generatedContentResponse.getCandidatesCount() == 0) {
                return null;
            }

            final String response = generatedContentResponse.getCandidates(0).getContent().getPartsList().get(0).getText();

            return objectMapper.readTree(response).get("summary").asText();
        }
    }
}
