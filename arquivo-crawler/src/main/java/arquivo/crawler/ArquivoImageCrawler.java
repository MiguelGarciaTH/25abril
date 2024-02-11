package arquivo.crawler;

import arquivo.model.IntegrationLog;
import arquivo.model.SearchEntity;
import arquivo.repository.*;
import arquivo.services.ContextualTextScoreService;
import arquivo.services.WebClientService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@EnableScheduling
public class ArquivoImageCrawler {

    private static final Logger LOG = LoggerFactory.getLogger(ArquivoImageCrawler.class);

    private final IntegrationLogRepository integrationLogRepository;
    private final SearchEntityRepository searchEntityRepository;

    private static final String ARQUIVO_IMAGE_API_BASE_URL = "https://arquivo.pt/imagesearch?q=%s&offset=0&maxItems=200&prettyPrint=true";
    private static final ContextualTextScoreService textScore = new ContextualTextScoreService();

    private final WebClientService webClientService;

    @Autowired
    public ArquivoImageCrawler(IntegrationLogRepository integrationLogRepository, SearchEntityRepository searchEntityRepository,
                               SiteRepository siteRepository, ChangelogRepository changeLogRepository,
                               RateLimiterRepository rateLimiterRepository,
                               KafkaTemplate<String, String> kafkaTemplate) {
        this.integrationLogRepository = integrationLogRepository;
        this.searchEntityRepository = searchEntityRepository;
        this.webClientService = new WebClientService(rateLimiterRepository);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void crawl() {
        final LocalDateTime today = LocalDateTime.now(ZoneOffset.UTC);
        final List<SearchEntity> entities = searchEntityRepository.findAll();

        int total = 0;
        for (SearchEntity entity : entities) {
            if (entity.getImageUrl() == null) {
                final String url = String.format(ARQUIVO_IMAGE_API_BASE_URL, entity.getName());
                LOG.info("Crawling image for: {} ({})", entity.getName(), entity.getType().name());

                try {
                    final JsonNode response = webClientService.get(url, "arquivo.pt");
                    int responseSize = response.get("responseItems").size();
                    //LOG.debug("Response for {}: {}", url, response.toPrettyString());
                    LOG.info("Crawling image results: {}: {}", entity.getName(), responseSize);
                    integrationLogRepository.save(new IntegrationLog(url, LocalDateTime.now(ZoneOffset.UTC), "crawler-image", IntegrationLog.Status.TS, "", "Total responses: " + responseSize));

                    final String imgSrc = getBestImage(entity, response.get("responseItems"));
                    entity.setImageUrl(imgSrc);
                    searchEntityRepository.save(entity);

                    total += responseSize;
                } catch (WebClientResponseException e) {
                    LOG.error("Failed to get {}", url);
                    integrationLogRepository.save(new IntegrationLog(url, LocalDateTime.now(ZoneOffset.UTC), "crawler-image", IntegrationLog.Status.TR, "", e.getMessage()));
                }
            }
        }
        final LocalDateTime finished = LocalDateTime.now(ZoneOffset.UTC);
        LOG.info("Finished: {} results founds in {} mins", total, ChronoUnit.MINUTES.between(today, finished));
    }

    private String getBestImage(SearchEntity entity, JsonNode responseItems) {
        final List<TitleScore> titleScores = new ArrayList<>();
        for (JsonNode node : responseItems) {
            if (node.has("pageTitle")) {
                int score = scoreImage(entity.getName(), node);
                titleScores.add(new TitleScore(entity.getName(), score, node.get("imgLinkToArchive").asText()));
            }

        }
        titleScores.sort(Comparator.comparingInt(TitleScore::score));
        return titleScores.get(titleScores.size() - 1).url();
    }

    private record TitleScore(String title, int score, String url) {

    }

    private int scoreImage(String name, JsonNode node) {
        int score = 0;
        if (node.has("imgSrc")) {
            String url = node.get("imgSrc").asText();
            score = scoreElem(name, url);
        }
        if (node.has("pageTitle")) {
            String title = node.get("pageTitle").asText();
            score = scoreElem(name, title);
        }
        if (node.has("imgAlt")) {
            String alt = node.get("imgAlt").get(0).asText();
            score += scoreElem(name, alt);
        }
        if (node.has("imgCaption")) {
            String caption = node.get("imgCaption").get(0).asText();
            score += scoreElem(name, caption);
            score += textScore.score(caption, null).total();
        }
        return score;
    }

    private int scoreElem(String name, String title) {
        if (title.equals(name)) {
            return 10;
        }
        if (title.contains(name)) {
            return 5;
        }
        int score = 0;
        String[] nameParts = name.split(" ");
        for (String part : nameParts) {
            if (title.contains(part)) {
                score++;
            }
        }
        return score;
    }
}
