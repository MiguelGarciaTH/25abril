package arquivo.crawler;

import arquivo.model.SearchEntity;
import arquivo.repository.RateLimiterRepository;
import arquivo.repository.SearchEntityRepository;
import arquivo.services.RateLimiterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

@Service
@EnableScheduling
public class DBPediaCrawler {

    private static final Logger LOG = LoggerFactory.getLogger(DBPediaCrawler.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private static final String dbPediaBaseUrl = "https://dbpedia.org/data/%s.json";
    private final SearchEntityRepository searchEntityRepository;
    private final RateLimiterService rateLimiterService;

    @Autowired
    public DBPediaCrawler(SearchEntityRepository searchEntityRepository, RateLimiterRepository rateLimiterRepository) {
        this.searchEntityRepository = searchEntityRepository;
        this.rateLimiterService = new RateLimiterService(rateLimiterRepository);

        final HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000 * 1000)
                .responseTimeout(Duration.ofSeconds(5000));

        webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(codecs -> codecs
                        .defaultCodecs()
                        .maxInMemorySize(1500 * 1024))
                .build();

        this.objectMapper = new ObjectMapper();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void crawl() {
        final LocalDateTime today = LocalDateTime.now(ZoneOffset.UTC);
        int total = 0;
        final List<SearchEntity> entities = searchEntityRepository.findAll();
        for (SearchEntity entity : entities) {
            if (entity.getBiography() == null) {
                LOG.info("Crawling bio for: {} ({})", entity.getName(), entity.getType().name());

                String nameDBPediaFormat = entity.getName().replaceAll(" ", "_");
                final String url = String.format(dbPediaBaseUrl, nameDBPediaFormat);

                rateLimiterService.increment("dbpedia");

                final JsonNode response = getDBPediaPage(url);
                String biography = null;
                if (response != null && !response.isEmpty()) {
                    if (response.has("http://dbpedia.org/resource/" + nameDBPediaFormat)) {
                        JsonNode rootNode = response.get("http://dbpedia.org/resource/" + nameDBPediaFormat);
                        //if (SearchEntity.Type.ARTISTAS == entity.getType() || SearchEntity.Type.POLITICOS == entity.getType()) {
                        if (rootNode.has("http://dbpedia.org/ontology/abstract")) {
                            JsonNode bioArray = rootNode.get("http://dbpedia.org/ontology/abstract");
                            for (var bio : bioArray) {
                                if ("pt".equals(bio.get("lang").asText())) {
                                    biography = bio.get("value").asText();
                                    LOG.debug("Entity {} has bio", entity.getName());
                                    total++;
                                }
                            }
                        }
                        //}
                    }
                }
                entity.setBiography(biography);
                searchEntityRepository.save(entity);
            }
        }
        LocalDateTime finished = LocalDateTime.now(ZoneOffset.UTC);
        LOG.info("Finished: {} results founds in {} mins", total, ChronoUnit.MINUTES.between(today, finished));
    }


    private JsonNode getDBPediaPage(String url) {
        try {
            final JsonNode response = objectMapper.readTree(webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve().bodyToMono(String.class).block());
            LOG.debug("Response for {} : {}", url, response);
            return response;
        } catch (JsonProcessingException e) {
            LOG.error("Problem fetching {}", url);
        } catch (WebClientResponseException e2) {
            LOG.error("Problem fetching {}", url, e2.getMessage());
        }
        return null;
    }
}
