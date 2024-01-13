package arquivo.processor;

import arquivo.model.Article;
import arquivo.model.CrawlerRecord;
import arquivo.model.SearchEntity;
import arquivo.model.Site;
import arquivo.repository.ArticleRepository;
import arquivo.repository.SearchEntityRepository;
import arquivo.repository.SiteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

@Service
@EnableKafka
public class ArquivoRecordListener {

    private static final Logger LOG = LoggerFactory.getLogger(ArquivoRecordListener.class);

    private final ObjectMapper objectMapper;
    private final ArticleRepository articleRepository;
    private final SiteRepository siteRepository;
    private final SearchEntityRepository searchEntityRepository;

    ArquivoRecordListener(ObjectMapper objectMapper, ArticleRepository articleRepository, SiteRepository siteRepository,
                          SearchEntityRepository searchEntityRepository) {
        this.objectMapper = objectMapper;
        this.articleRepository = articleRepository;
        this.siteRepository = siteRepository;
        this.searchEntityRepository = searchEntityRepository;
    }

    @KafkaListener(topics = {"${processor.topic}"},
            containerFactory = "kafkaListenerContainerFactory")
    public void listener(ConsumerRecord<String, String> record, Acknowledgment ack) {

        CrawlerRecord event = null;
        try {
            event = objectMapper.readValue(record.value(), CrawlerRecord.class);
            LOG.debug("Received on topic {} record {}:{}", record.topic(), record.key(), event);

            Article article = articleRepository.findByDigest(event.digest()).orElse(null);
            if (article == null) {
                final Site site = siteRepository.findById(event.siteId()).orElse(null);
                final SearchEntity searchEntity = searchEntityRepository.findById(event.searchEntityId()).orElse(null);
                article = new Article(event.digest(), event.url(), event.noFrameUrl(), event.textUrl(), event.metaDataUrl(), LocalDateTime.now(ZoneOffset.UTC), site);
                article.setArticleEntityAssociation(Set.of(searchEntity));
                articleRepository.save(article);
            } else {
                // do stuff later
                // update
            }


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        ack.acknowledge();
    }
}
