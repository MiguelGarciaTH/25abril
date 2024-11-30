package arquivo.crawler;

import arquivo.model.IntegrationLog;
import arquivo.model.SearchEntity;
import arquivo.repository.IntegrationLogRepository;
import arquivo.repository.RateLimiterRepository;
import arquivo.repository.SearchEntityRepository;
import arquivo.services.RateLimiterService;
import arquivo.services.WebClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@EnableScheduling
@ConditionalOnProperty(name = "25-abril.dbpedia.bio-crawler.enable", havingValue = "true")
public class DBPediaCrawler {

    private static final Logger LOG = LoggerFactory.getLogger(DBPediaCrawler.class);
    private static final String dbPediaBaseUrl = "https://dbpedia.org/data/%s.json";
    private final SearchEntityRepository searchEntityRepository;
    private final IntegrationLogRepository integrationLogRepository;
    private final WebClientService webClientService;

    @Autowired
    public DBPediaCrawler(SearchEntityRepository searchEntityRepository, RateLimiterRepository rateLimiterRepository,
                          IntegrationLogRepository integrationLogRepository) {
        this.searchEntityRepository = searchEntityRepository;
        this.integrationLogRepository = integrationLogRepository;
        this.webClientService = new WebClientService(rateLimiterRepository);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void crawl() {
        final LocalDateTime today = LocalDateTime.now(ZoneOffset.UTC);
        int total = 0;
        final List<SearchEntity> entities = searchEntityRepository.findAll();
        for (SearchEntity entity : entities) {
            if (entity.getBiography() == null) {
                LOG.info("Crawling bio for: {} ({})", entity.getName(), entity.getType().name());

                final List<String> names = new ArrayList<>();
                names.add(entity.getName());
                if (entity.getAliases() != null) {
                    Collections.addAll(names, entity.getAliases().split(","));
                }
                for (String name : names) {
                    String nameDBPediaFormat = name.replaceAll(" ", "_");
                    String url = String.format(dbPediaBaseUrl, nameDBPediaFormat);
                    JsonNode response = webClientService.get(url, "dbpedia");
                    String biography = getDBPediaBio(nameDBPediaFormat, response);
                    integrationLogRepository.save(new IntegrationLog(url, LocalDateTime.now(ZoneOffset.UTC), "dbPediaCrawler", IntegrationLog.Status.TS, "", response.toString()));
                    if (biography != null) {
                        total++;
                        LOG.debug("Entity {} has bio", entity.getName());
                        entity.setBiography(biography);
                        searchEntityRepository.save(entity);
                        break;
                    }
                }
            }
        }
        LocalDateTime finished = LocalDateTime.now(ZoneOffset.UTC);
        LOG.info("Finished: {} results founds in {} mins", total, ChronoUnit.MINUTES.between(today, finished));
    }

    private String getDBPediaBio(String nameDBPediaFormat, JsonNode response) {
        if (response != null && !response.isEmpty() && (response.has("http://dbpedia.org/resource/" + nameDBPediaFormat))) {
            JsonNode rootNode = response.get("http://dbpedia.org/resource/" + nameDBPediaFormat);
            if (rootNode.has("http://dbpedia.org/ontology/abstract")) {
                JsonNode bioArray = rootNode.get("http://dbpedia.org/ontology/abstract");
                for (var bio : bioArray) {
                    if ("pt".equals(bio.get("lang").asText())) {
                        return bio.get("value").asText();
                    }
                }
            }
        }
        return null;
    }
}
