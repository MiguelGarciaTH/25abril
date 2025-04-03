package arquivo.image;

import arquivo.model.Article;
import arquivo.model.records.ImageRecord;
import arquivo.model.IntegrationLog;
import arquivo.repository.ArticleRepository;
import arquivo.repository.IntegrationLogRepository;
import arquivo.services.MetricService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@EnableKafka
public class ArquivoImageListener {

    private static final Logger LOG = LoggerFactory.getLogger(ArquivoImageListener.class);

    private final ObjectMapper objectMapper;
    private final ArticleRepository articleRepository;
    private final MetricService metricService;
    private final IntegrationLogRepository integrationLogRepository;

    private long receivedCounter;
    private long imageNewCounter;
    private long imageNullCounter;
    private long imageAlreadySetCounter;
    private long jsonErrors;
    private final Path directory = Paths.get("images/");


    ArquivoImageListener(ObjectMapper objectMapper, ArticleRepository articleRepository,
                         IntegrationLogRepository integrationLogRepository, MetricService metricService) {
        this.objectMapper = objectMapper;
        this.articleRepository = articleRepository;
        this.integrationLogRepository = integrationLogRepository;
        this.metricService = metricService;

        receivedCounter = metricService.loadValue("image_total_articles_received");
        imageNewCounter = metricService.loadValue("image_total_new");
        imageAlreadySetCounter = metricService.loadValue("image_total_articles_repeated");
        jsonErrors = metricService.loadValue("image_total_articles_error_json_parsing");
        imageNullCounter = metricService.loadValue("image_total_articles_error_null_image");
    }

    @KafkaListener(topics = {"${image-crop.topic}"}, containerFactory = "kafkaListenerContainerFactory", concurrency = "5")
    public void listener(ConsumerRecord<String, String> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_PARTITION) String partition) {

        LOG.info("Received on topic {} on partition {} record {}", record.topic(), partition, record.value());
        receivedCounter++;

        final ImageRecord event;
        try {
            event = objectMapper.readValue(record.value(), ImageRecord.class);
        } catch (JsonProcessingException e) {
            LOG.error("Error parsing json record: {}", record.value());
            jsonErrors++;
            ack.acknowledge();
            return;
        }

        final Article article = articleRepository.findById(event.articleId()).orElse(null);
        if (article == null) {
            LOG.warn("Should not happen article: {} is null", event.articleId());
            ack.acknowledge();
            return;
        }

        if (!article.hasImage()) {
            final String fileName = (article.getUrl().hashCode() & Integer.MAX_VALUE) + ".png";
            if (!fileExists(fileName)) {
                cropImage(fileName, event.imageUrl());
            }
            article.setHasImage(true);
            article.setImagePath("images/" + fileName);
            articleRepository.save(article);
            imageNewCounter++;
            integrationLogRepository.save(new IntegrationLog(event.imageUrl(), LocalDateTime.now(ZoneOffset.UTC), "processor-image", IntegrationLog.Status.TS, "", null));
        } else {
            imageAlreadySetCounter++;
        }

        ack.acknowledge();
        logStats();
        storeStats();
    }

    private boolean fileExists(String filename) {
        final Path filePath = directory.resolve(filename);
        return (Files.exists(filePath) && Files.isRegularFile(filePath));
    }

    private void cropImage(String fileName, String imageUrl) {
        URL url;
        try {
            url = new URL(imageUrl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        BufferedImage image;
        try {
            image = ImageIO.read(url);
        } catch (IOException e) {
            imageNullCounter++;
            integrationLogRepository.save(new IntegrationLog(imageUrl, LocalDateTime.now(ZoneOffset.UTC), "processor-image", IntegrationLog.Status.TE, "", e.getMessage()));
            throw new RuntimeException(e);
        }
        final BufferedImage dest = image.getSubimage(0, 0, image.getWidth(), Math.min(image.getHeight() / 2, (image.getWidth() + (image.getWidth() / 2))));
        final File outputfile = new File("arquivo-web/public/images/" + fileName);
        try {
            ImageIO.write(dest, "png", outputfile);
        } catch (IOException e) {
            integrationLogRepository.save(new IntegrationLog(imageUrl, LocalDateTime.now(ZoneOffset.UTC), "processor-image", IntegrationLog.Status.TE, "", e.getMessage()));
            throw new RuntimeException(e);
        }

    }

    private void logStats() {
        LOG.info("Stats:");
        LOG.info("Articles received: {}", receivedCounter);
        LOG.info("Articles new image : {}/{}", imageNewCounter, receivedCounter);

        if (imageAlreadySetCounter > 0) {
            LOG.warn("Article already set image : {}/{}", imageAlreadySetCounter, receivedCounter);
        }
        if (imageNullCounter > 0) {
            LOG.warn("Articles image returned null : {}/{}", imageNullCounter, receivedCounter);
        }
        if (jsonErrors > 0) {
            LOG.warn("Json parsing errors : {}/{}", jsonErrors, receivedCounter);
        }
    }

    private void storeStats() {
        metricService.setValue("image_total_articles_received", receivedCounter);
        metricService.setValue("image_total_new", imageNewCounter);
        metricService.setValue("image_total_articles_repeated_image", imageAlreadySetCounter);
        metricService.setValue("image_total_articles_error_json_parsing", jsonErrors);
        metricService.setValue("image_total_articles_error_null_image", imageNullCounter);
    }
}

