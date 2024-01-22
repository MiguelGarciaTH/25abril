package arquivo.crawler;

import arquivo.model.SearchEntity;
import arquivo.repository.SearchEntityRepository;
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
import java.util.List;

@Service
@EnableScheduling
public class DBPediaCrawler {

    private static final Logger LOG = LoggerFactory.getLogger(DBPediaCrawler.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final static String dbPediaBaseUrl = "https://dbpedia.org/data/%s.json";
    private SearchEntityRepository searchEntityRepository;

    @Autowired
    public DBPediaCrawler(SearchEntityRepository searchEntityRepository) {
        this.searchEntityRepository = searchEntityRepository;

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
        final List<SearchEntity> entities = searchEntityRepository.findAll();
        for (SearchEntity entity : entities) {
            if (entity.getImageUrl() == null || entity.getImageUrl().isBlank() || entity.getImageUrl().isEmpty()) {
                String nameDBPediaFormat = entity.getName().replaceAll(" ", "_");
                final String url = String.format(dbPediaBaseUrl, nameDBPediaFormat);
                final JsonNode response = getDBPediaPage(1, url);
                String imgUrl = null;
                if (response != null && !response.isEmpty())
                    if (response.has("http://dbpedia.org/resource/" + nameDBPediaFormat)) {
                        JsonNode rootNode = response.get("http://dbpedia.org/resource/" + nameDBPediaFormat);
                        if (rootNode.has("http://dbpedia.org/ontology/thumbnail")) {
                            imgUrl = rootNode.get("http://dbpedia.org/ontology/thumbnail").get(0).get("value").asText().replace("?width=300", "");
                            LOG.info("Entity {} has image {}", entity.getName(), imgUrl);
                        }
                    }
                entity.setImageUrl(imgUrl);
                searchEntityRepository.save(entity);
            }
        }
    }


    private JsonNode getDBPediaPage(int step, String url) {
        try {
            final JsonNode response = objectMapper.readTree(webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve().bodyToMono(String.class).block());
            LOG.debug("[{}] Response for {} : {}", step, url, response);
        } catch (JsonProcessingException e) {
            LOG.error("Problem fetching {}", url);
        } catch (WebClientResponseException e2) {
            LOG.error("Problem fetching {}", url, e2.getMessage());
        }
        return null;
    }
}
