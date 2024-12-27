package arquivo.crawler;

import arquivo.model.IntegrationLog;
import arquivo.model.SearchEntity;
import arquivo.repository.*;
import arquivo.services.TextScoreService;
import arquivo.services.WebClientService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(name = "25-abril.arquivo.images-crawler.enable", havingValue = "true")
public class ArquivoImageCrawler {

    private static final Logger LOG = LoggerFactory.getLogger(ArquivoImageCrawler.class);

    private final IntegrationLogRepository integrationLogRepository;
    private final SearchEntityRepository searchEntityRepository;

    private static final String ARQUIVO_IMAGE_API_BASE_URL = "https://arquivo.pt/imagesearch?q=%s&offset=0&maxItems=200&prettyPrint=true";
    private static final TextScoreService textScore = TextScoreService.getInstance();

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
            score += textScore.searchEntityscore(null, null, caption, null).total();
        }
        if (node.has("imgHeight") && node.has("imgWidth")) {
            score += scoreImageSize(node.get("imgHeight").asInt(), node.get("imgWidth").asInt());
        }
        return score;
    }

    private int scoreImageSize(int height, int width) {
        int imageArea = height * width;
        int score = 0;
        if (imageArea <= 2500) { // 50*50
            return score;
        }
        if (imageArea > 2500 && imageArea <= 10000) { // 100*100
            score = 1;
        }
        if (imageArea > 10000 && imageArea <= 40000) { // 200*200
            score = 3;
        }
        if (imageArea > 40000 && imageArea <= 90000) { // 300*300
            score = 4;
        }
        if (imageArea > 90000 && imageArea <= 160000) { // 400*400
            score = 5;
        }
        if (imageArea > 160000 && imageArea <= 250000) { // 500*500
            score = 6;
        }
        if (imageArea > 250000 && imageArea <= 360000) { // 600*600
            score = 7;
        }
        if (imageArea >= 360000) { // > 600*600
            score = 10;
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
