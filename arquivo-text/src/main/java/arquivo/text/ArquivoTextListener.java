package arquivo.text;

import arquivo.model.*;
import arquivo.repository.*;
import arquivo.services.TextScoreService;
import arquivo.services.MetricService;
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
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
@EnableKafka
public class ArquivoTextListener {

    private static final Logger LOG = LoggerFactory.getLogger(ArquivoTextListener.class);

    private final ObjectMapper objectMapper;
    private final ArticleRepository articleRepository;
    private final ArticleSearchEntityAssociationRepository articleSearchEntityAssociationRepository;
    private final TextScoreService textScoreService = TextScoreService.getInstance();
    private final SearchEntityRepository searchEntityRepository;
    private final KeywordRepository keywordRepository;
    private final ArticleKeywordRepository articleKeywordRepository;
    private final WebClientService webClientService;
    private final MetricService metricService;
    private final IntegrationLogRepository integrationLogRepository;

    private long receivedCounter;
    private long summaryNullCounter;
    private long vertexApiErrorCounter;
    private long summaryAlreadySetCounter;
    private long newSummaryCounter;
    private long newAssociationCounter;
    private long duplicatedArticleEntity;
    private long jsonErrors;
    private long discardedSummaryCounter;

    final DecimalFormat decimalFormat = new DecimalFormat("#.###########");

    private final GenerativeModel model;

    private final Map<String, Object> input;
    private int retry = 0;

    ArquivoTextListener(ObjectMapper objectMapper, ArticleRepository articleRepository,
                        ArticleSearchEntityAssociationRepository articleSearchEntityAssociationRepository,
                        SearchEntityRepository searchEntityRepository,
                        KeywordRepository keywordRepository, MetricService metricService, ArticleKeywordRepository articleKeywordRepository,
                        IntegrationLogRepository integrationLogRepository) {
        this.objectMapper = objectMapper;
        objectMapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        this.articleRepository = articleRepository;
        this.articleSearchEntityAssociationRepository = articleSearchEntityAssociationRepository;
        this.searchEntityRepository = searchEntityRepository;
        this.keywordRepository = keywordRepository;
        this.articleKeywordRepository = articleKeywordRepository;
        this.integrationLogRepository = integrationLogRepository;
        this.metricService = metricService;
        this.webClientService = new WebClientService();

        GenerationConfig generationConfig = GenerationConfig.newBuilder()
                .setMaxOutputTokens(1024)
                .setTemperature(0.2F)
                .setTopP(0.95F)
                .setResponseMimeType("application/json")
                .build();
        List<SafetySetting> safetySettings = Arrays.asList(
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
        Content systemInstruction = ContentMaker.fromMultiModalData("* Translate to Portuguese from Portugal, not from Portuguese from Brazil\n* Just focus on the summary\n* Keep the summary between 1000 and 1500 words");

        VertexAI vertexAi = new VertexAI("sincere-hearth-442713-p7", "europe-west2");
        model = new GenerativeModel.Builder()
                .setModelName("gemini-1.5-flash-002")
                .setVertexAi(vertexAi)
                .setGenerationConfig(generationConfig)
                .setSafetySettings(safetySettings)
                .setSystemInstruction(systemInstruction)
                .build();

        input = new HashMap<>();
        input.put("language", "pt");
        input.put("max_ngram_size", 3);
        input.put("number_of_keywords", 5);

        receivedCounter = metricService.loadValue("text_total_articles_received");
        newSummaryCounter = metricService.loadValue("text_total_articles_new_summary");
        discardedSummaryCounter = metricService.loadValue("text_total_articles_discarded_summary");
        summaryAlreadySetCounter = metricService.loadValue("text_total_articles_repeated_summary");
        duplicatedArticleEntity = metricService.loadValue("text_total_articles_repeated_association");
        vertexApiErrorCounter = metricService.loadValue("text_total_articles_error_vertex_ai");
        jsonErrors = metricService.loadValue("text_total_articles_error_json_parsing");
        summaryNullCounter = metricService.loadValue("text_total_articles_error_null_summary");
    }

    @KafkaListener(topics = {"${text.topic}"}, containerFactory = "kafkaListenerContainerFactory", concurrency = "5")
    public void listener(ConsumerRecord<String, String> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {

        LOG.info("Received on topic {} on partition {} record {}", record.topic(), partition, record.value());
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

        final SearchEntity searchEntity = searchEntityRepository.findById(event.searchEntityId()).orElse(null);
        if (searchEntity == null) {
            LOG.warn("Should not happen searchEntity: {} is null", event.searchEntityId());
            ack.acknowledge();
            return;
        }

        final ArticleSearchEntityAssociation articleSearchEntityAssociation = articleSearchEntityAssociationRepository.findByArticleIdAndSearchEntityId(article.getId(), searchEntity.getId()).orElse(null);
        if (articleSearchEntityAssociation != null) {
            LOG.warn("Record repeated we already set summary and score for article: {} and search entity: {} ", article.getId(), searchEntity.getId());
            duplicatedArticleEntity++;
            ack.acknowledge();
            return;
        }

        String summary = article.getSummary();

        if (summary == null || summary.isBlank()) {
            try {
                // process summary
                String summaryResponse = summarize(article.getText());
                if (summaryResponse == null) {
                    LOG.error("Vertex returned a null summary: {}", event.articleId());
                    ack.acknowledge();
                    summaryNullCounter++;
                    return;
                }

                summary = getSummary(summaryResponse);

                // keep retrying : 3x
                while (summary == null && retry < 3) {
                    LOG.error("Error parsing json (on retry {}): {} summary: {}", retry, event.articleId(), summary);
                    Thread.sleep(500L);
                    summaryResponse = summarize(article.getText());
                    summary = getSummary(summaryResponse);
                    retry++;
                }

                // give up on summary
                if (summary == null) {
                    LOG.error("Give up on parsing json (after retry {}): {} summary: {}", retry, event.articleId(), summary);
                    integrationLogRepository.save(new IntegrationLog("", LocalDateTime.now(ZoneOffset.UTC), "text", IntegrationLog.Status.TR, summaryResponse, null));
                    ack.acknowledge();
                    retry = 0;
                    jsonErrors++;
                    return;
                }
                retry = 0;
                article.setSummary(summary);

            } catch (IOException e) {
                LOG.error("Error using Vertex API: {}", event.articleId(), e);
                integrationLogRepository.save(new IntegrationLog("", LocalDateTime.now(ZoneOffset.UTC), "text", IntegrationLog.Status.TR, article.getText(), e.getMessage()));
                ack.acknowledge();
                vertexApiErrorCounter++;
                return;
            } catch (InterruptedException e) {
                // do nothing
            }

            // score for article summary
            final TextScoreService.Score contextualScore = textScoreService.contextualScore(article.getTitle(), article.getUrl(), summary);
            if (contextualScore.total() == 0) {
                // the previous score (greater than zero) was due to some side-text-artifacts on the article (e.g., headlines on side columns)
                LOG.info("Article {} is not about 25 de Abril (summary scoring) previous score: {}", event.articleId(), decimalFormat.format(article.getContextualScore()));

                // we are saving the article summary just to avoid keeping repeating unnecessary calls to vertex AI
                article.setSummary(summary);
                article.setSummaryScore(0.0);
                articleRepository.save(article);

                discardedSummaryCounter++;
                ack.acknowledge();
                return;
            }

            final JsonNode contextualScoreJson = objectMapper.convertValue(contextualScore.keywordCounter(), JsonNode.class);
            article.setSummaryScore(contextualScore.total());
            article.setSummaryScoreDetails(contextualScoreJson);

            article = articleRepository.save(article);
            newSummaryCounter++;

            // keywords by yake
            final List<KeywordScore> keywordList = getKeywords(summary);
            for (var ks : keywordList) {
                if (ks.score() > 0.0) {
                    Keyword keyword = keywordRepository.findByKeyword(ks.keyword());
                    if (keyword == null) {
                        keyword = keywordRepository.save(new Keyword(ks.keyword()));
                    }
                    articleKeywordRepository.save(new ArticleKeyword(article, keyword, ks.score()));
                }
            }

        } else {
            summaryAlreadySetCounter++;
        }

        final TextScoreService.Score entityScore = textScoreService.searchEntityscore(article.getTitle(), article.getUrl(), summary, searchEntity);
        final JsonNode entityScoreJson = objectMapper.convertValue(entityScore.keywordCounter(), JsonNode.class);
        articleSearchEntityAssociationRepository.save(new ArticleSearchEntityAssociation(article, searchEntity, entityScore.total(), entityScoreJson));
        newAssociationCounter++;

        ack.acknowledge();
        logStats();
        storeStats();
    }

    private String getSummary(String summaryResponse) {
        final JsonNode responseJson;
        try {
            responseJson = objectMapper.readTree(summaryResponse);
        } catch (JsonProcessingException e) {
            return null;
        }
        if (responseJson.has("summary")) {
            return responseJson.get("summary").asText();
        } else if (responseJson.has("resumo")) {
            return responseJson.get("resumo").asText();
        } else if (responseJson.has("sumario")) {
            return responseJson.get("sumario").asText();
        } else if (responseJson.has("Resumo")) {
            return responseJson.get("Resumo").asText();
        } else if (responseJson.has("sumário")) {
            return responseJson.get("sumário").asText();
        } else if (responseJson.has("summaries")) {
            return responseJson.get("summaries").get(0).asText();//return first
        } else {
            return null;
        }
    }

    private void logStats() {
        LOG.info("Stats:");
        LOG.info("Texts received: {}", receivedCounter);
        LOG.info("Texts new summary : {}/{}", newSummaryCounter, receivedCounter);
        LOG.info("Texts discarded summary : {}/{}", discardedSummaryCounter, receivedCounter);
        LOG.info("Texts new association : {}/{}", newAssociationCounter, receivedCounter);
        LOG.info("Texts repeated association : {}/{}", duplicatedArticleEntity, receivedCounter);

        if (summaryAlreadySetCounter > 0) {
            LOG.warn("Texts already set summary : {}/{}", summaryAlreadySetCounter, receivedCounter);
        }
        if (vertexApiErrorCounter > 0) {
            LOG.warn("Texts api error : {}/{}", vertexApiErrorCounter, receivedCounter);
        }
        if (summaryNullCounter > 0) {
            LOG.warn("Texts summary returned null : {}/{}", summaryNullCounter, receivedCounter);
        }
        if (jsonErrors > 0) {
            LOG.warn("Json parsing errors : {}/{}", jsonErrors, receivedCounter);
        }
    }

    private void storeStats() {
        metricService.setValue("text_total_articles_received", receivedCounter);
        metricService.setValue("text_total_articles_new_summary", newSummaryCounter);
        metricService.setValue("text_total_articles_discarded_summary", discardedSummaryCounter);
        metricService.setValue("text_total_articles_repeated_summary", summaryAlreadySetCounter);
        metricService.setValue("text_total_articles_repeated_association", duplicatedArticleEntity);
        metricService.setValue("text_total_articles_error_vertex_ai", vertexApiErrorCounter);
        metricService.setValue("text_total_articles_error_json_parsing", jsonErrors);
        metricService.setValue("text_total_articles_error_null_summary", summaryNullCounter);
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
        var content = ContentMaker.fromMultiModalData(text);
        GenerateContentResponse generatedContentResponse = model.generateContent(content);
        if (generatedContentResponse.getCandidatesCount() == 0) {
            return null;
        }
        return generatedContentResponse.getCandidates(0).getContent().getPartsList().get(0).getText();
    }
}

