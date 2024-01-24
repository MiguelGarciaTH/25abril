package arquivo.processor;

import arquivo.RateLimiter;
import arquivo.model.*;
import arquivo.repository.ArticleRepository;
import arquivo.repository.IntegrationLogRepository;
import arquivo.repository.SearchEntityRepository;
import arquivo.repository.SiteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@EnableKafka
public class ArquivoRecordListener {

    private static final Logger LOG = LoggerFactory.getLogger(ArquivoRecordListener.class);

    private final ObjectMapper objectMapper;
    private final ArticleRepository articleRepository;
    private final SiteRepository siteRepository;
    private final SearchEntityRepository searchEntityRepository;
    private final List<String> stopwords;
    private final ContextualTextScore contextualTextScore;
    private final RateLimiter rateLimiter;
    private final IntegrationLogRepository integrationLogRepository;
    private int totalReceived = 0;
    private int totalAccepted = 0;
    private int totalRejected = 0;

    ArquivoRecordListener(ObjectMapper objectMapper, ArticleRepository articleRepository, SiteRepository siteRepository,
                          SearchEntityRepository searchEntityRepository, IntegrationLogRepository integrationLogRepository) {
        this.objectMapper = objectMapper;
        this.articleRepository = articleRepository;
        this.siteRepository = siteRepository;
        this.searchEntityRepository = searchEntityRepository;
        this.integrationLogRepository = integrationLogRepository;

        this.stopwords = load("portuguese_stopwords.txt");
        this.contextualTextScore = new ContextualTextScore();
        this.rateLimiter = new RateLimiter(240, 60_000L);
    }

    @KafkaListener(topics = {"${processor.topic}"},
            containerFactory = "kafkaListenerContainerFactory")
    public void listener(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            final CrawlerRecord event = objectMapper.readValue(record.value(), CrawlerRecord.class);
            LOG.debug("Received on topic {} record {}:{}", record.topic(), record.key(), event);

            final SearchEntity searchEntity = searchEntityRepository.findById(event.searchEntityId()).orElse(null);
            Article article = articleRepository.findByTitleAndSiteId(event.title(), event.siteId()).orElse(null);
            if (article == null) {
                totalReceived++;

                String text = getText(event.textUrl());
                text = removeAllStopwords(text);

                ContextualTextScore.Score score = contextualTextScore.score(text);
                if (score.total() > 20) { // 20 just to discard totally unrelated. The real filter will be done via REST service
                    totalAccepted++;
                    final Site site = siteRepository.findById(event.siteId()).orElse(null);
                    article = new Article(event.digest(), event.title(), score.total(),
                            objectMapper.convertValue(score.individualScore(), JsonNode.class),
                            event.url(), event.noFrameUrl(), event.textUrl(), event.metaDataUrl(),
                            LocalDateTime.now(ZoneOffset.UTC), site);
                    article.setArticleEntityAssociation(Set.of(searchEntity));
                } else {
                    totalRejected++;
                    ack.acknowledge();
                    return;
                }

            } else {
                LOG.debug("Article already exists article id {}", article.getId());
                article.setArticleEntityAssociation(Set.of(searchEntity));
            }
            articleRepository.save(article);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ack.acknowledge();
        LOG.info("Accepted articles: {}/{} Rejected articles: {}/{} ", totalAccepted, totalReceived, totalRejected, totalReceived);
    }

    private String getText(String urlInput) {
        final String inputTrim = urlInput.substring(0, Math.min(urlInput.length(), 240));
        try {
            final URL url = new URL(urlInput);
            final StringBuilder everything = new StringBuilder();
            final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                everything.append(line);
            }
            rateLimiter.increment();
            in.close();

            integrationLogRepository.save(new IntegrationLog(inputTrim, LocalDateTime.now(ZoneOffset.UTC), "processor", IntegrationLog.Status.TS, "", null));

            return everything.toString();
        } catch (MalformedURLException e) {
            LOG.error("Mal formed url {}: {}", urlInput, e.getMessage());
            integrationLogRepository.save(new IntegrationLog(inputTrim, LocalDateTime.now(ZoneOffset.UTC), "processor", IntegrationLog.Status.TR, "", e.getMessage()));
        } catch (IOException e) {
            LOG.error("I/O error for url {}: {}", urlInput, e.getMessage());
            integrationLogRepository.save(new IntegrationLog(inputTrim, LocalDateTime.now(ZoneOffset.UTC), "processor", IntegrationLog.Status.TR, "", e.getMessage()));
        }
        return null;
    }

    private String removeAllStopwords(String data) {
        final ArrayList<String> allWords =
                Stream.of(data.split(" "))
                        .collect(Collectors.toCollection(ArrayList<String>::new));
        allWords.removeAll(stopwords);
        return String.join(" ", allWords);
    }

    private List<String> load(String path) {
        final ClassLoader classLoader = ResourceLoader.class.getClassLoader();
        final File file = new File(classLoader.getResource(path).getFile());
        try {
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
