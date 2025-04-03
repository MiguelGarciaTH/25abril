package arquivo.crawler;

import arquivo.model.records.CrawlerRecord;
import arquivo.model.IntegrationLog;
import arquivo.model.SearchEntity;
import arquivo.model.Site;
import arquivo.repository.*;
import arquivo.services.MetricService;
import arquivo.services.WebClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "25-abril.arquivo.article-crawler.enable", havingValue = "true")
public class ArquivoCrawler {

    private static final Logger LOG = LoggerFactory.getLogger(ArquivoCrawler.class);
    private final ObjectMapper objectMapper;

    private final IntegrationLogRepository integrationLogRepository;
    private final SearchEntityRepository searchEntityRepository;
    private final SiteRepository siteRepository;
    private final ArticleRepository articleRepository;
    private final MetricService metricService;
    private final WebClientService webClientService;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final DateTimeFormatter arquivoFormatter = DateTimeFormatter.ofPattern("uuuuMMddHHmmss");

    private long totalFetchedCounter;
    private long noTitleCounter;
    private long duplicateCounter;
    private long totalSentCounter;

    @Value("${processor.topic}")
    private String topic;

    @Autowired
    public ArquivoCrawler(IntegrationLogRepository integrationLogRepository, SearchEntityRepository searchEntityRepository,
                          SiteRepository siteRepository, ArticleRepository articleRepository,
                          RateLimiterRepository rateLimiterRepository,
                          MetricService metricService,
                          KafkaTemplate<String, String> kafkaTemplate) {
        this.integrationLogRepository = integrationLogRepository;
        this.searchEntityRepository = searchEntityRepository;
        this.siteRepository = siteRepository;
        this.articleRepository = articleRepository;
        this.metricService = metricService;
        this.kafkaTemplate = kafkaTemplate;
        this.webClientService = new WebClientService(rateLimiterRepository);
        this.objectMapper = new ObjectMapper();

        totalFetchedCounter = metricService.loadValue("crawler_total_articles_fetched");
        noTitleCounter = metricService.loadValue("crawler_total_articles_no_title");
        duplicateCounter = metricService.loadValue("crawler_total_articles_duplicates");
        totalSentCounter = metricService.loadValue("crawler_total_articles_sent_to_processor");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void crawl() {
        final LocalDateTime start = LocalDateTime.now(ZoneOffset.UTC);
        final List<SearchEntity> entities = searchEntityRepository.findAll();
        final List<Site> sites = siteRepository.findAll();
        final List<UrlRecord> urls = prepareSearchUrl(entities, sites);
        // shuffling aims to optimize the messages sent to kafka. If we shuffle we may avoid sending duplicated ahead.
        Collections.shuffle(urls);

        LOG.info("Number of URLs to hit Arquivo.pt {} (#entities:{} and #sites:{})", urls.size(), entities.size(), sites.size());

        for (UrlRecord url : urls) {
            LOG.info("Start crawling: {} ({}) (site: {})", url.queryTerm(), url.entity.getType().name(), url.site.getName());
            try {
                int entityTotal;
                LOG.debug("Request for {}", url.url);
                JsonNode response = webClientService.get(url.url, "arquivo.pt");

                // publish to kafka
                final List<JsonNode> responses = new ArrayList<>();
                responses.add(response.get("response_items"));

                entityTotal = response.get("response_items").size();
                int pages = 1;
                if (response.has("next_page")) {
                    do {
                        String nextPageUrl = java.net.URLDecoder.decode(response.get("next_page").asText(), StandardCharsets.UTF_8);
                        response = webClientService.get(nextPageUrl, "arquivo.pt");
                        responses.add(response.get("response_items"));
                        entityTotal += response.get("response_items").size();
                        pages++;
                    } while (response.has("next_page"));
                }

                for (JsonNode node : responses) {
                    publish(url, node);
                }

                LOG.info("Crawling results: {} (site: {}) : {} in {} pages", url.entity.getName(), url.site.getName(), entityTotal, pages);
                totalFetchedCounter += entityTotal;
            } catch (WebClientResponseException e) {
                LOG.error("Failed to get {}", url);
                integrationLogRepository.save(new IntegrationLog(url.url, LocalDateTime.now(ZoneOffset.UTC), "crawler", IntegrationLog.Status.TR, "", e.getMessage()));
            }
        }
        final LocalDateTime finished = LocalDateTime.now(ZoneOffset.UTC);
        LOG.info("Finished crawling: {} results founds in {} mins", totalFetchedCounter, ChronoUnit.MINUTES.between(start, finished));
        LOG.info("Stats:");
        LOG.info("\tArticles with no titles: {}/{}", noTitleCounter, totalFetchedCounter);
        LOG.info("\tArticles already stored: {}/{}", duplicateCounter, totalFetchedCounter);
        LOG.info("\tArticles sent: {}/{}", totalSentCounter, totalFetchedCounter);

        metricService.setValue("crawler_total_articles_fetched", totalFetchedCounter);
        metricService.setValue("crawler_total_articles_no_title", noTitleCounter);
        metricService.setValue("crawler_total_articles_duplicates", duplicateCounter);
        metricService.setValue("crawler_total_articles_sent_to_processor", totalSentCounter);

    }

    private boolean shouldSendToKafka(int entityId, int siteId, JsonNode result) {
        final String title = result.get("title").asText();
        // accepting only articles with title
        if (title == null || title.isBlank()) {
            noTitleCounter++;
            LOG.debug("Article with no title (total: {})", noTitleCounter);
            return false;
        }
        if (articleRepository.existsByTitleAndSiteAndEntityId(title, siteId, entityId)) {
            duplicateCounter++;
            LOG.debug("Article already exist (total: {})", duplicateCounter);
            return false;
        }
        return true;
    }

    private void publish(UrlRecord url, JsonNode responseItems) {
        for (var responseItem : responseItems) {
            final int siteId = url.site.getId();
            final int entityId = url.entity.getId();
            if (shouldSendToKafka(entityId, siteId, responseItem)) {
                final CrawlerRecord record = new CrawlerRecord(entityId, siteId, responseItem.get("title").asText(), responseItem.get("linkToArchive").asText(),
                        responseItem.get("linkToExtractedText").asText(), responseItem.get("linkToScreenshot").asText());
                try {
                    kafkaTemplate.send(topic, objectMapper.writeValueAsString(record));
                    totalSentCounter++;
                    LOG.debug("Sent to topic {} value={}", topic, record);
                } catch (JsonProcessingException e) {
                    LOG.warn("Error processing {} and response item: {}", url.url, responseItem.toPrettyString());
                }
            }
        }
    }

    private List<UrlRecord> prepareSearchUrl(List<SearchEntity> entities, List<Site> sites) {
        final List<UrlRecord> urls = new ArrayList<>();
        for (SearchEntity entity : entities) {
            for (Site site : sites) {
                final List<UrlRecord> urlsInt = buildUrl(entity, site);
                if (!urlsInt.isEmpty()) {
                    urls.addAll(urlsInt);
                }
            }
        }
        return urls;
    }

    private List<UrlRecord> buildUrl(SearchEntity entity, Site site) {
        final List<UrlRecord> urls = new ArrayList<>();
        final LocalDateTime startDate = LocalDateTime.parse("19960101000000", arquivoFormatter);
        final LocalDateTime endDate = LocalDateTime.now(ZoneOffset.UTC);
        final String baseUrl = "https://arquivo.pt/textsearch?q=\"%s\"&prettyPrint=false&siteSearch=%s&from=%s&to=%s&maxItems=500&type=html&dedupValue=1&dedupField=title&fields=title,linkToArchive,linkToExtractedText,linkToScreenshot";
        String url = String.format(baseUrl, entity.getName(), site.getUrl(), startDate.format(arquivoFormatter), endDate.format(arquivoFormatter));
        urls.add(new UrlRecord(entity, entity.getName(), site, startDate, endDate, url));
        if (entity.getAliases() != null) {
            for (String alias : entity.getAliases().split(",")) {
                url = String.format(baseUrl, alias, site.getUrl(), startDate.format(arquivoFormatter), endDate.format(arquivoFormatter));
                urls.add(new UrlRecord(entity, alias, site, startDate, endDate, url));
            }
        }
        return urls;
    }

    record UrlRecord(SearchEntity entity, String queryTerm, Site site, LocalDateTime startDate, LocalDateTime endDate,
                     String url) {
    }
}
