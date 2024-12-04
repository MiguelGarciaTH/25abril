package arquivo.processor;

import arquivo.model.*;
import arquivo.repository.*;
import arquivo.services.ContextualTextScoreService;
import arquivo.services.RateLimiterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@EnableKafka
public class ArquivoRecordListener {

    private static final Logger LOG = LoggerFactory.getLogger(ArquivoRecordListener.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${text-summary.topic}")
    private String topic;

    private final ObjectMapper objectMapper;
    private final ArticleRepository articleRepository;
    private final SiteRepository siteRepository;
    private final SearchEntityRepository searchEntityRepository;
    private final ArticleSearchEntityAssociationRepository articleSearchEntityAssociationRepository;
    private final RateLimiterService rateLimiterService;
    private final IntegrationLogRepository integrationLogRepository;

    private int receivedCounter = 0;
    private int duplicatesCounter = 0;
    private int reusedCounter = 0;
    private int newCounter = 0;
    private int emptyTextCounter = 0;
    private int retryCounter = 0;
    private int totalSentCounter = 0;
    private int discardedCounter = 0;

    private final HashMap<Integer, Site> siteCache = new HashMap<>();
    private final HashMap<Integer, SearchEntity> entityCache = new HashMap<>();

    private final ContextualTextScoreService scoreService = ContextualTextScoreService.getInstance();

    ArquivoRecordListener(ObjectMapper objectMapper, ArticleRepository articleRepository, SiteRepository siteRepository,
                          SearchEntityRepository searchEntityRepository,
                          ArticleSearchEntityAssociationRepository articleSearchEntityAssociationRepository,
                          IntegrationLogRepository integrationLogRepository,
                          RateLimiterRepository rateLimiterRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.articleRepository = articleRepository;
        this.siteRepository = siteRepository;
        this.searchEntityRepository = searchEntityRepository;
        this.articleSearchEntityAssociationRepository = articleSearchEntityAssociationRepository;
        this.integrationLogRepository = integrationLogRepository;
        this.kafkaTemplate = kafkaTemplate;

        this.rateLimiterService = new RateLimiterService(rateLimiterRepository);

        final List<SearchEntity> searchEntities = searchEntityRepository.findAll();
        LOG.info("Pre chaching names patterns for {} search entities", searchEntities.size());
    }

    @KafkaListener(topics = {"${processor.topic}"}, containerFactory = "kafkaListenerContainerFactory")
    public void listener(ConsumerRecord<String, String> record, Acknowledgment ack) {

        LOG.debug("Received on topic {} record {}", record.topic(), record.value());
        receivedCounter++;

        final CrawlerRecord event;
        try {
            event = objectMapper.readValue(record.value(), CrawlerRecord.class);
        } catch (JsonProcessingException e) {
            LOG.error("Error parsing json record: {}", record.value());
            ack.acknowledge();
            return;
        }

        if (articleRepository.existsByTrimmedUrlAndSiteAndEntityId(trimUrl(event.url()), event.siteId(), event.searchEntityId())) {
            LOG.warn("Article already exists we will skip it: {} (trimmed url: {})", event.title(), event.url());
            duplicatesCounter++;
            ack.acknowledge();
            return;
        }

        Article article = articleRepository.findByTrimmedUrlAndSiteId(trimUrl(event.url()), event.siteId()).orElse(null);
        if (article != null) { // TODO good candidate for index
            final SearchEntity searchEntity = getSearchEntity(event.searchEntityId());
            articleSearchEntityAssociationRepository.save(new ArticleSearchEntityAssociation(article, searchEntity));
            reusedCounter++;
            LOG.debug("New article association articleId={} title={} url={} for entity={}", article.getId(), event.title(), event.url(), searchEntity.getName());

        } else { //new article
            final Site site = getSite(event.siteId());
            final String text = getText(event.textUrl());
            if (text == null || text.isBlank()) {
                LOG.warn("Text is empty for article: {} (trimmed url: {})", event.title(), event.url());
                emptyTextCounter++;
                ack.acknowledge();
                return;
            }

            final SearchEntity searchEntity = getSearchEntity(event.searchEntityId());
            final ContextualTextScoreService.Score score = scoreService.score(event.title(), event.textUrl(), text, event.searchEntityId(), mergeNamesToList(searchEntity));
            if (score.total() > 5) {
                final JsonNode scoreJson = objectMapper.convertValue(score.keywordCounter(), JsonNode.class);
                article = articleRepository.saveAndFlush(new Article(event.title(), event.url(), trimUrl(event.url()), LocalDateTime.now(ZoneOffset.UTC), site, score.total(), scoreJson));
                articleSearchEntityAssociationRepository.save(new ArticleSearchEntityAssociation(article, searchEntity));

                LOG.debug("New article articleId={} title={} url={} with score={}", article.getId(), event.title(), event.url(), score);
                newCounter++;
                publish(new TextRecord(article.getId(), text));
            } else {
                discardedCounter++;
            }
        }

        ack.acknowledge();

        logStats();
    }

    private List<String> mergeNamesToList(SearchEntity searchEntity) {
        final List<String> names = new ArrayList<String>();
        names.add(searchEntity.getName().toLowerCase());
        if (searchEntity.getAliases() != null) {
            String[] namesArrays = searchEntity.getAliases().split(",");
            for (String name : namesArrays) {
                names.add(name.toLowerCase());
            }
        }
        return names;
    }

    private Site getSite(int siteId) {
        return siteCache.computeIfAbsent(siteId, k -> siteRepository.getReferenceById(siteId));
    }

    private SearchEntity getSearchEntity(int searchEntityId) {
        return entityCache.computeIfAbsent(searchEntityId, k -> searchEntityRepository.findById(searchEntityId).orElse(null));
    }

    private String getText(String urlInput) {
        if (retryCounter > 2) {
            retryCounter = 0;
            return null;
        }
        final String inputTrim = urlInput.substring(0, Math.min(urlInput.length(), 240));
        rateLimiterService.increment("arquivo.pt");
        try {
            return get(inputTrim, urlInput);
        } catch (IOException e) {
            LOG.error("IOException url {}: {}", urlInput, e.getMessage());
            integrationLogRepository.save(new IntegrationLog(inputTrim, LocalDateTime.now(ZoneOffset.UTC), "processor", IntegrationLog.Status.TR, "", e.getMessage()));
            LOG.info("Will retry {}^nt attempt (max 2): {}", retryCounter, urlInput);
            try {
                Thread.sleep(500L);
            } catch (InterruptedException ex) {
                //do nothing
            }
            try {
                retryCounter++;
                return get(inputTrim, urlInput);
            } catch (IOException ex) {
                LOG.error("Error fetching text", ex.getCause());
            }
        }
        return null;
    }

    private String get(String inputTrim, String urlInput) throws IOException {
        try (InputStream in = new URL(urlInput).openStream()) {
            byte[] bytes = in.readAllBytes();
            String allText = new String(bytes, StandardCharsets.UTF_8);
            integrationLogRepository.save(new IntegrationLog(inputTrim, LocalDateTime.now(ZoneOffset.UTC), "processor", IntegrationLog.Status.TS, "", null));
            return allText;
        }
    }

    private void publish(TextRecord textRecord) {
        try {
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(textRecord));
            totalSentCounter++;
            LOG.debug("Sent to topic {} value={}", topic, textRecord);
        } catch (JsonProcessingException e) {
            LOG.warn("Error processing article {} for summary processing", textRecord.articleId());
        }
    }

    private String trimUrl(String url) {
        return url.split("\\/\\/")[2];
    }

    private void logStats() {
        LOG.info("Stats:");
        LOG.info("Articles received: {}", receivedCounter);
        LOG.info("Articles new: {}/{}", newCounter, receivedCounter);
        LOG.info("Articles re-used: {}/{}", reusedCounter, receivedCounter);
        LOG.info("Articles sent to summary: {}/{}", totalSentCounter, receivedCounter);

        if (duplicatesCounter > 0) {
            LOG.warn("Articles repeated warn: {}/{}", duplicatesCounter, receivedCounter);
        }
        if (emptyTextCounter > 0) {
            LOG.warn("Articles without text: {}/{}", emptyTextCounter, receivedCounter);
        }
        if (discardedCounter > 0) {
            LOG.warn("Articles discarded: {}/{}", discardedCounter, receivedCounter);
        }
    }
}
