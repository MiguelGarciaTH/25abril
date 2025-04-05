package arquivo.crawler;

import arquivo.model.IntegrationLog;
import arquivo.model.SearchEntity;
import arquivo.repository.IntegrationLogRepository;
import arquivo.repository.RateLimiterRepository;
import arquivo.repository.SearchEntityRepository;
import arquivo.services.TextScoreService;
import arquivo.services.WebClientService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@EnableScheduling
@ConditionalOnProperty(name = "25-abril.arquivo.images-crawler.enable", havingValue = "true")
public class ArquivoImageCrawler {

    private static final Logger LOG = LoggerFactory.getLogger(ArquivoImageCrawler.class);

    private final IntegrationLogRepository integrationLogRepository;
    private final SearchEntityRepository searchEntityRepository;

    private static final String ARQUIVO_IMAGE_API_BASE_URL = "https://arquivo.pt/imagesearch?q=\"%s\"&offset=0&maxItems=100&size=lg&prettyPrint=true&type=jpg,png&siteSearch=*.publico.pt,*.expresso.pt,*.tsf.pt,*.dn.pt,*.jn.pt,*.observador.pt";
    private static final TextScoreService textScore = TextScoreService.getInstance();

    private final WebClientService webClientService;
    private final FaceDetection faceDetection;

    @Autowired
    public ArquivoImageCrawler(IntegrationLogRepository integrationLogRepository, SearchEntityRepository searchEntityRepository,
                               RateLimiterRepository rateLimiterRepository, FaceDetection faceDetection) {
        this.integrationLogRepository = integrationLogRepository;
        this.searchEntityRepository = searchEntityRepository;
        this.webClientService = new WebClientService(rateLimiterRepository);
        this.faceDetection = faceDetection;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void crawl() {
        final LocalDateTime today = LocalDateTime.now(ZoneOffset.UTC);
        final List<SearchEntity> entities = searchEntityRepository.findAll();

        int total = 0;
        for (SearchEntity entity : entities) {
            if (entity.getImageUrl() == null) {
                String url = String.format(ARQUIVO_IMAGE_API_BASE_URL, entity.getName());
                LOG.info("Crawling image for: {} ({})", entity.getName(), entity.getType().name());
                try {
                    int numerberOfImages = 0;
                    List<ImageScore> images = new ArrayList<>();
                    JsonNode response;
                    do {
                        response = webClientService.get(url, "arquivo-image.pt");
                        if (response != null) {
                            images.addAll(scoreImages(entity, response.get("responseItems")));
                            numerberOfImages += response.get("responseItems").size();
                            url = response.get("nextPage").asText();
                        }
                    } while (response != null && response.has("nextPage") && !response.get("nextPage").asText().equals(response.get("previousPage").asText()));

                    LOG.info("Crawling image results: {}: {}", entity.getName(), numerberOfImages);
                    integrationLogRepository.save(new IntegrationLog(url, LocalDateTime.now(ZoneOffset.UTC), "crawler-image", IntegrationLog.Status.TS, "", "Total responses: " + numerberOfImages));
                    final String imgSrc = getBestImage(images);
                    entity.setImageUrl(imgSrc);
                    searchEntityRepository.save(entity);

                    total += numerberOfImages;
                } catch (WebClientResponseException e) {
                    LOG.error("Failed to get {}", url);
                    integrationLogRepository.save(new IntegrationLog(url, LocalDateTime.now(ZoneOffset.UTC), "crawler-image", IntegrationLog.Status.TR, "", e.getMessage()));
                }
            }
        }
        final LocalDateTime finished = LocalDateTime.now(ZoneOffset.UTC);
        LOG.info("Finished: {} results founds in {} mins", total, ChronoUnit.MINUTES.between(today, finished));
    }

    private List<ImageScore> scoreImages(SearchEntity entity, JsonNode responseItems) {
        final List<ImageScore> titleScores = new ArrayList<>();
        for (JsonNode node : responseItems) {
            if (node.has("pageTitle")) {
                ImageScore imageScore = scoreImage(entity, node);
                if (imageScore.score > 0) {
                    System.out.println(imageScore);
                    titleScores.add(imageScore);
                }
            }
        }
        return titleScores;
    }

    private String getBestImage(List<ImageScore> titleScores) {
        titleScores.sort(Comparator.comparingDouble(ImageScore::score));
        if (!titleScores.isEmpty()) {
            if (titleScores.size() == 1) {
                return titleScores.get(0).url();
            }

            //should return the last element (with the highest score)
            return titleScores.get(titleScores.size() - 1).url();
        }
        return null;
    }

    private record ImageScore(String title, double score, String url, Map<String, Double> imageScore) {

    }

    private ImageScore scoreImage(SearchEntity entity, JsonNode node) {
        double score = 0;
        double confidence = 0.0;
        final Map<String, Double> scoreDetails = new HashMap<>();
        if (node.has("imgSrc")) {
            final String url = URLDecoder.decode(node.get("imgSrc").asText());
            double count = textScore.searchEntityscore(url, entity).total();
            score += count;
            scoreDetails.put("url", count);
            confidence = faceDetection.detectFace(url);
            scoreDetails.put("faces", confidence);
            if (confidence == 0.0) {
                return new ImageScore(entity.getName(), 0, node.get("imgLinkToArchive").asText(), Map.of("No faces", 0.0));
            }
        }

        if (node.has("pageTitle")) {
            final String title = node.get("pageTitle").asText();
            double count = textScore.searchEntityscore(title, entity).total();
            score += count;
            scoreDetails.put("title", count);
        }
        if (node.has("imgAlt")) {
            double count = 0.0;
            for (var altElem : node.get("imgAlt")) {
                final String alt = altElem.asText();
                count += textScore.searchEntityscore(alt, entity).total();
            }
            score += count;
            scoreDetails.put("alt", count);
        }
        if (node.has("imgCaption")) {
            double count = 0.0;
            for (var captionElem : node.get("imgCaption")) {
                final String caption = captionElem.asText();
                count += textScore.searchEntityscore(caption, entity).total();
            }
            score += count;
            scoreDetails.put("caption", count);
        }
        if (node.has("imgHeight") && node.has("imgWidth") && score > 0.0) {
            double count = scoreImageSize(node.get("imgHeight").asInt(), node.get("imgWidth").asInt());
            score += count;
            scoreDetails.put("size", count);
        }

        // multiply confidence
        score = score * confidence;

        return new ImageScore(entity.getName(), score, node.get("imgLinkToArchive").asText(), scoreDetails);
    }

    private double scoreImageSize(int height, int width) {
        int imageArea = height * width;
        double score = 0;
        if (imageArea <= 2500) { // 50*50
            return score;
        }
        if (imageArea <= 10000) { // 100*100
            score = 0.5;
        }
        if (imageArea > 10000 && imageArea <= 40000) { // 200*200
            score = 0.7;
        }
        if (imageArea > 40000 && imageArea <= 90000) { // 300*300
            score = 0.9;
        }
        if (imageArea > 90000 && imageArea <= 160000) { // 400*400
            score = 1.0;
        }
        if (imageArea > 160000 && imageArea <= 250000) { // 500*500
            score = 1.2;
        }
        if (imageArea > 250000 && imageArea <= 360000) { // 600*600
            score = 1.5;
        }
        if (imageArea >= 360000) { // > 600*600
            score = 1.8;
        }
        return score;
    }
}
