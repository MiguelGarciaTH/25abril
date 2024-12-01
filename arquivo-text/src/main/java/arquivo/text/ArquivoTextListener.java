package arquivo.text;

import arquivo.model.TextRecord;
import arquivo.repository.ArticleRepository;
import arquivo.repository.IntegrationLogRepository;
import arquivo.repository.RateLimiterRepository;
import arquivo.services.RateLimiterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@EnableKafka
public class ArquivoTextListener {

    private static final Logger LOG = LoggerFactory.getLogger(ArquivoTextListener.class);

    private final ObjectMapper objectMapper;
    private final ArticleRepository articleRepository;
    private final IntegrationLogRepository integrationLogRepository;
    private final RateLimiterService rateLimiterService;

    private int receivedCounter = 0;

    ArquivoTextListener(ObjectMapper objectMapper, ArticleRepository articleRepository,
                        IntegrationLogRepository integrationLogRepository,
                        RateLimiterRepository rateLimiterRepository) {
        this.objectMapper = objectMapper;
        this.articleRepository = articleRepository;
        this.integrationLogRepository = integrationLogRepository;

        this.rateLimiterService = new RateLimiterService(rateLimiterRepository);

    }

    @KafkaListener(topics = {"${text.topic}"}, containerFactory = "kafkaListenerContainerFactory")
    public void listener(ConsumerRecord<String, String> record, Acknowledgment ack) {

        LOG.debug("Received on topic {} record {}", record.topic(), record.value());
        receivedCounter++;

        final TextRecord event;
        try {
            event = objectMapper.readValue(record.value(), TextRecord.class);
        } catch (JsonProcessingException e) {
            LOG.error("Error parsing json record: {}", record.value());
            ack.acknowledge();
            return;
        }


        ack.acknowledge();

        logStats();
    }

    private void logStats() {
        LOG.info("Stats:");
        LOG.info("Texts received: {}", receivedCounter);
    }
}
