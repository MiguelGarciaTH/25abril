package arquivo.text;

import arquivo.model.*;
import arquivo.repository.ArticleKeywordRepository;
import arquivo.repository.ArticleRepository;
import arquivo.repository.KeywordRepository;
import arquivo.repository.SearchEntityRepository;
import arquivo.services.ContextualTextScoreService;
import arquivo.services.WebClientService;
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
import java.util.*;

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
    private final KeywordRepository keywordRepository;
    private final ArticleKeywordRepository articleKeywordRepository;
    private final WebClientService webClientService;

    private int receivedCounter = 0;
    private int summaryNullCounter = 0;
    private int vertexApiErrorCounter = 0;
    private int summaryAlreadySetCounter = 0;
    private int newSummaryCounter = 0;

    private final Map<String, Object> input;

    ArquivoTextListener(ObjectMapper objectMapper, ArticleRepository articleRepository, SearchEntityRepository searchEntityRepository,
                        KeywordRepository keywordRepository, ArticleKeywordRepository articleKeywordRepository) {
        this.objectMapper = objectMapper;
        objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        this.articleRepository = articleRepository;
        this.searchEntityRepository = searchEntityRepository;
        this.keywordRepository = keywordRepository;
        this.articleKeywordRepository = articleKeywordRepository;
        this.webClientService = new WebClientService();

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

        input = new HashMap<>();
        input.put("language", "pt");
        input.put("max_ngram_size", 3);
        input.put("number_of_keywords", 5);
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

        Article article = articleRepository.findById(event.articleId()).orElse(null);
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
            final JsonNode scoreJson = objectMapper.convertValue(score.keywordCounter(), JsonNode.class);

            article.setSummary(summary);
            article.setSummaryScore(score.total());
            article.setSummaryScoreDetails(scoreJson);
            article = articleRepository.save(article);

            final List<KeywordScore> keywordList = getKeywords(summary);
            final List<ArticleKeyword> keywords = new ArrayList<>(keywordList.size());
            for (var ks : keywordList) {
                Keyword keyword = keywordRepository.findByKeyword(ks.keyword);
                if (keyword == null) {
                    keyword = keywordRepository.save(new Keyword(ks.keyword));
                }
                keywords.add(new ArticleKeyword(article, keyword, ks.score()));
            }
            articleKeywordRepository.saveAll(keywords);
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

    private List<KeywordScore> getKeywords(String text) {
        input.put("text", text);
        final JsonNode keywords = webClientService.post("http://localhost:5000/yake/", objectMapper.convertValue(input, JsonNode.class));
        final List<KeywordScore> keywordList = new ArrayList<>(keywords.size());
        for (var keyword : keywords) {
            keywordList.add(new KeywordScore(keyword.get("ngram").asText(), keyword.get("score").asDouble()));
        }
        return keywordList;
    }

    private record KeywordScore(String keyword, Double score) {

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
