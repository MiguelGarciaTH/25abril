package arquivo.services;

import arquivo.repository.RateLimiterRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

public class WebClientService {

    private static final Logger LOG = LoggerFactory.getLogger(WebClientService.class);

    private final WebClient webClient;
    private final RateLimiterService rateLimiterService;
    private final ObjectMapper objectMapper;

    public WebClientService(RateLimiterRepository rateLimiterRepository) {
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
        this.rateLimiterService = new RateLimiterService(rateLimiterRepository);
    }

    public JsonNode get(String url, String service) {
        try {
            rateLimiterService.increment(service);

            final JsonNode response = objectMapper.readTree(webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve().bodyToMono(String.class).block());
            LOG.debug("Response for {} : {}", url, response);
            return response;
        } catch (JsonProcessingException e) {
            LOG.error("Problem fetching {}", url);
        } catch (WebClientResponseException e2) {
            LOG.error("Problem fetching {}: {}", url, e2.getMessage());
        }
        return null;
    }
}
