package arquivo.crawler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Component
@EnableScheduling
public class ArquivoCrawler {

    private static final Logger LOG = LoggerFactory.getLogger(ArquivoCrawler.class);
    private final WebClient webClient;
    private ObjectMapper objectMapper;

    ArquivoCrawler() {

        final HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000 * 1000)
                .responseTimeout(Duration.ofSeconds(5000));

        webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        this.objectMapper = new ObjectMapper();
    }

    @Scheduled(fixedRate = 10000)
    public void test() throws JsonProcessingException {
        LOG.info("Starting crawling");
        String url = "https://arquivo.pt/textsearch?versionHistory=http://publico.pt";
        final JsonNode response = objectMapper.readTree(webClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(String.class).block());

        System.out.println(response.toPrettyString());
        LOG.info("Finished crawling");
    }

}
