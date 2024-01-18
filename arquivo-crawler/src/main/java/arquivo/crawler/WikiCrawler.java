package arquivo.crawler;

import arquivo.model.SearchEntity;
import arquivo.repository.SearchEntityRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
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
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.*;

@Service
@EnableScheduling
public class WikiCrawler {

    private static final Logger LOG = LoggerFactory.getLogger(WikiCrawler.class);
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final static String wikiBaseUrl = "https://en.wikipedia.org/w/api.php?action=parse&page=%s&format=json&prop=images";
    private final static String wikiImageBaseUrl = "https://commons.wikimedia.org/w/api.php?action=query&prop=imageinfo&iiprop=url&redirects&format=json&titles=File:%s";
    private SearchEntityRepository searchEntityRepository;

    @Autowired
    public WikiCrawler(SearchEntityRepository searchEntityRepository) {
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

    //@EventListener(ApplicationReadyEvent.class)
    public void crawl() {
        final List<SearchEntity> entities = searchEntityRepository.findAll();
        for (SearchEntity entity : entities) {
            if (entity.getImageUrl() == null || entity.getImageUrl().isBlank() || entity.getImageUrl().isEmpty()) {
                String imgUrl = getWikiUrl(entity.getName());
                entity.setImageUrl(imgUrl);
                searchEntityRepository.save(entity);
            }
        }
    }

    private String getWikiUrl(String name) {

        System.out.println("NAME=" + name + "\n");
        final String url = String.format(wikiBaseUrl, name);

        final JsonNode response = get(1, url);

        if (response.has("error")) {
            return null;
        }
        final int wikiPageId = response.get("parse").get("pageid").asInt();
        JsonNode images = response.get("parse").get("images");
        if (images.size() == 0) {
            return null;
        }

        System.out.println("IMAGE NAME=" + images.get(0));
        final String urlImage = String.format(wikiImageBaseUrl, images.get(0).asText());
        final JsonNode response2 = get(2, urlImage);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();
        addKeys("", response2, map, new ArrayList<>());

        String findKey=null;
        for(String key : map.keySet()){
            if(key.contains("url")){
                findKey = map.get(key);
            }
        }

        return findKey;
    }

    private JsonNode get(int step, String url) {
        JsonNode response = null;
        try {
            response = objectMapper.readTree(webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve().bodyToMono(String.class).block());
            LOG.debug("[{}] Response for {} : {}", step, url, response);
        } catch (JsonProcessingException e) {
            LOG.error("Problem fetching {}", url);
        }
        return response;
    }

    private void addKeys(String currentPath, JsonNode jsonNode, Map<String, String> map, List<Integer> suffix) {
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();
            String pathPrefix = currentPath.isEmpty() ? "" : currentPath + "-";

            while (iter.hasNext()) {
                Map.Entry<String, JsonNode> entry = iter.next();
                addKeys(pathPrefix + entry.getKey(), entry.getValue(), map, suffix);
            }
        } else if (jsonNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) jsonNode;

            for (int i = 0; i < arrayNode.size(); i++) {
                suffix.add(i + 1);
                addKeys(currentPath, arrayNode.get(i), map, suffix);

                if (i + 1 <arrayNode.size()){
                    suffix.remove(arrayNode.size() - 1);
                }
            }

        } else if (jsonNode.isValueNode()) {
            if (currentPath.contains("-")) {
                for (int i = 0; i < suffix.size(); i++) {
                    currentPath += "-" + suffix.get(i);
                }

                suffix = new ArrayList<>();
            }

            ValueNode valueNode = (ValueNode) jsonNode;
            map.put(currentPath, valueNode.asText());
        }
    }
}
