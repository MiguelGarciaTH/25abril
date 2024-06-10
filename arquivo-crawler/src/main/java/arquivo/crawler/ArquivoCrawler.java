package arquivo.crawler;

import arquivo.model.*;
import arquivo.repository.*;
import arquivo.services.WebClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Component
@EnableScheduling
public class ArquivoCrawler {

    private static final Logger LOG = LoggerFactory.getLogger(ArquivoCrawler.class);
    private final ObjectMapper objectMapper;

    private final IntegrationLogRepository integrationLogRepository;
    private final SearchEntityRepository searchEntityRepository;
    private final SiteRepository siteRepository;
    private final ChangelogRepository changeLogRepository;

    private final WebClientService webClientService;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final DateTimeFormatter arquivoFormatter = DateTimeFormatter.ofPattern("uuuuMMddHHmmss");

    private long noTitleCounter = 0;

    @Value("${crawler.topic}")
    private String topic;

    @Autowired
    public ArquivoCrawler(IntegrationLogRepository integrationLogRepository, SearchEntityRepository searchEntityRepository,
                          SiteRepository siteRepository, ChangelogRepository changeLogRepository,
                          RateLimiterRepository rateLimiterRepository,
                          KafkaTemplate<String, String> kafkaTemplate) {
        this.integrationLogRepository = integrationLogRepository;
        this.searchEntityRepository = searchEntityRepository;
        this.siteRepository = siteRepository;
        this.changeLogRepository = changeLogRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.webClientService = new WebClientService(rateLimiterRepository);
        this.objectMapper = new ObjectMapper();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void crawl() {
        final LocalDateTime today = LocalDateTime.now(ZoneOffset.UTC);
        final List<SearchEntity> entities = searchEntityRepository.findAll();
        final List<Site> sites = siteRepository.findAll();

        int total = 0;
        final List<UrlRecord> urls = prepareSearchUrl(entities, sites);
        for (UrlRecord url : urls) {
            LOG.info("Start crawling: {} ({}) (site: {})", url.queryTerm(), url.entity.getType().name(), url.site.getName());
            try {
                int entityTotal = 0;
                LOG.debug("Request for {}", url.url);
                JsonNode response = webClientService.get(url.url, "arquivo.pt");

                // update change log
                saveChangeLog(url, response);

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
                    publishToKafka(url, node);
                }

                LOG.info("Crawling results: {} (site: {}) : {} in {} pages", url.entity.getName(), url.site.getName(), entityTotal, pages);
                total += entityTotal;
            } catch (WebClientResponseException e) {
                LOG.error("Failed to get {}", url);
                integrationLogRepository.save(new IntegrationLog(url.url, LocalDateTime.now(ZoneOffset.UTC), "crawler", IntegrationLog.Status.TR, "", e.getMessage()));
            }
        }
        final LocalDateTime finished = LocalDateTime.now(ZoneOffset.UTC);
        LOG.info("Finished crawling: {} results founds in {} mins", total, ChronoUnit.MINUTES.between(today, finished));
    }

    private void publishToKafka(UrlRecord url, JsonNode response) {
        for (var node : response) {
            final int siteId = url.site.getId();
            final int entityId = url.entity.getId();

            final String arquivoTitle = node.get("title").asText();
            // accepting only articles with title
            if (arquivoTitle == null || arquivoTitle.isBlank()) {
                noTitleCounter++;
                LOG.debug("Articles with no title {}", noTitleCounter);
                return;
            }

            final CrawlerRecord record = new CrawlerRecord(entityId, siteId, arquivoTitle, node.get("linkToArchive").asText(),
                    node.get("originalURL").asText(), node.get("linkToMetadata").asText(), node.get("linkToExtractedText").asText(),
                    node.get("linkToNoFrame").asText());

            try {
                kafkaTemplate.send(topic, objectMapper.writeValueAsString(record));
                LOG.debug("Sent to topic {} value={}", topic, record);
            } catch (JsonProcessingException e) {
                LOG.warn("Error processing {} and response item: {}", url.url, node.toPrettyString());
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
        final LocalDateTime endDate = LocalDateTime.now(ZoneOffset.UTC);
        Changelog changeLog = changeLogRepository.findBySearchEntityIdAndSiteId(entity.getId(), site.getId()).orElse(null);

        LocalDateTime startDate;
        if (changeLog == null) {
            changeLog = new Changelog(LocalDateTime.parse("19960101000000", arquivoFormatter), endDate, site, entity);
            startDate = LocalDateTime.parse("19960101000000", arquivoFormatter);
        } else {
            if (isSameDay(endDate, changeLog.getToTimestamp())) {
                return urls;
            }
            startDate = changeLog.getToTimestamp();
        }

        final String baseUrl = "https://arquivo.pt/textsearch?q=\"%s\"&prettyPrint=false&siteSearch=%s&from=%s&to=%s&maxItems=500&type=html&dedupValue=1&dedupField=title";
        String url = String.format(baseUrl, entity.getName(), site.getUrl(), startDate.format(arquivoFormatter), endDate.format(arquivoFormatter));
        urls.add(new UrlRecord(entity, entity.getName(), site, changeLog, startDate, endDate, url));
        if (entity.getAliases() != null) {
            for (String alias : entity.getAliases().split(",")) {
                url = String.format(baseUrl, alias, site.getUrl(), startDate.format(arquivoFormatter), endDate.format(arquivoFormatter));
                urls.add(new UrlRecord(entity, alias, site, changeLog, startDate, endDate, url));
            }
        }
        return urls;
    }

    public static boolean isSameDay(LocalDateTime timestamp, LocalDateTime timestampToCompare) {
        return timestamp.truncatedTo(DAYS).isEqual(timestampToCompare.truncatedTo(DAYS));
    }

    private void saveChangeLog(UrlRecord url, JsonNode response) {
        final Changelog changelog = url.changeLog;
        int urlTotalResponses = changelog.getTotalEntries() + response.get("response_items").size();
        changelog.setTotalEntries(urlTotalResponses);
        changelog.setToTimestamp(url.endDate);
        changeLogRepository.save(changelog);
    }

    record UrlRecord(SearchEntity entity, String queryTerm, Site site, Changelog changeLog, LocalDateTime startDate,
                     LocalDateTime endDate, String url) {
        UrlRecord(UrlRecord urlRecord, String url) {
            this(urlRecord.entity, urlRecord.queryTerm, urlRecord.site, urlRecord.changeLog, urlRecord.startDate, urlRecord.endDate, url);
        }
    }
}
