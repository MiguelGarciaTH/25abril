package arquivo.processor;

import arquivo.model.*;
import arquivo.repository.*;
import arquivo.services.ContextualTextScoreService;
import arquivo.services.RateLimiterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@EnableKafka
public class ArquivoRecordListener {

    private static final Logger LOG = LoggerFactory.getLogger(ArquivoRecordListener.class);

    private final ObjectMapper objectMapper;
    private final ArticleRepository articleRepository;
    private final SiteRepository siteRepository;
    private final SearchEntityRepository searchEntityRepository;
    private final ArticleSearchEntityAssociationRepository articleSearchEntityAssociationRepository;
    private final ContextualTextScoreService contextualTextScoreService;
    private final RateLimiterService rateLimiterService;
    private final IntegrationLogRepository integrationLogRepository;
    private int noTitleCounter = 0;
    private int totalReceived = 0;
    private int totalAccepted = 0;
    private int totalRejected = 0;
    private int totalDuplicates = 0;
    private int totalTextFromWeb = 0;
    private int totalTextFromDB = 0;
    private int totalErrors = 0;
    private int retryCounter = 0;

    private final HashMap<Integer, List<String>> nameAliases = new HashMap<>();
    private final HashMap<Integer, Pattern> namePatterns = new HashMap<>();

    private static final String noTitleTemplate = "Sem título (#%s)";

    ArquivoRecordListener(ObjectMapper objectMapper, ArticleRepository articleRepository, SiteRepository siteRepository,
                          SearchEntityRepository searchEntityRepository,
                          ArticleSearchEntityAssociationRepository articleSearchEntityAssociationRepository,
                          IntegrationLogRepository integrationLogRepository,
                          RateLimiterRepository rateLimiterRepository) {
        this.objectMapper = objectMapper;
        this.articleRepository = articleRepository;
        this.siteRepository = siteRepository;
        this.searchEntityRepository = searchEntityRepository;
        this.articleSearchEntityAssociationRepository = articleSearchEntityAssociationRepository;
        this.integrationLogRepository = integrationLogRepository;

        this.contextualTextScoreService = ContextualTextScoreService.getInstance();
        this.rateLimiterService = new RateLimiterService(rateLimiterRepository);

        final List<SearchEntity> searchEntities = searchEntityRepository.findAll();
        LOG.info("Pre chaching names patterns for {} search entities", searchEntities.size());
        for (SearchEntity searchEntity : searchEntities) {
            if (!nameAliases.containsKey(searchEntity.getId())) {
                nameAliases.put(searchEntity.getId(), mergeNamesToList(searchEntity));
            }
            if (!namePatterns.containsKey(searchEntity.getId())) {
                namePatterns.put(searchEntity.getId(), buildPattern(searchEntity.getName(), searchEntity.getAliases()));
            }
        }
    }

    @KafkaListener(topics = {"${processor.topic}"}, containerFactory = "kafkaListenerContainerFactory")
    public void listener(ConsumerRecord<String, String> record, Acknowledgment ack) {
        totalReceived++;

        try {
            final CrawlerRecord event = objectMapper.readValue(record.value(), CrawlerRecord.class);
            LOG.debug("Received on topic {} record {}:{}", record.topic(), record.key(), event);

            final Site site = siteRepository.findById(event.siteId()).orElse(null);

            final String trimmedUrl = trimUrl(event.originalUrl());
            LOG.debug("Original title={} Trimmed title={} siteId={}", event.originalUrl(), trimmedUrl, site.getId());
            Article article = articleRepository.findByOriginalUrl(trimmedUrl, site.getId()).orElse(null);

            String text;
            String title;
            if (article == null) { // new article
                text = getText(event.textUrl());
                totalTextFromWeb++;
                if (text != null) {
                    title = getTitle(event, site);
                } else {
                    totalErrors++;
                    ack.acknowledge();
                    return;
                }
            } else { // already known article
                text = article.getText();
                title = article.getTitle();
                totalTextFromDB++;
            }

            final SearchEntity searchEntity = searchEntityRepository.findById(event.searchEntityId()).orElse(null);

            final ContextualTextScoreService.Score score = contextualTextScoreService.score(title, event.url(), text.toLowerCase(), searchEntity.getId(), nameAliases.get(searchEntity.getId()));
            if (score.total() > 10) {
                totalAccepted++;
                if (article == null) {
                    article = articleRepository.saveAndFlush(new Article(event.digest(), title, event.title(), event.url(), trimmedUrl, event.noFrameUrl(), event.textUrl(), text, event.metaDataUrl(), LocalDateTime.now(ZoneOffset.UTC), site));
                    LOG.debug("New article articleId={} title={} url={}", article.getId(), title, event.originalUrl());
                } else {
                    totalDuplicates++;
                    LOG.debug("Article already exists article id {} original title={} new title={}", article.getId(), article.getOriginalTitle(), title);
                }
                final ArticleSearchEntityAssociation articleSearchEntityAssociation = articleSearchEntityAssociationRepository.findByArticleIdAndSearchEntityId(article.getId(), searchEntity.getId()).orElse(null);
                if (articleSearchEntityAssociation == null) {
                    final String contextText = extractRelevantText(article.getText(), namePatterns.get(searchEntity.getId()));
                    LOG.debug("New association articleId={} entityId={}", article.getId(), searchEntity.getId());
                    articleSearchEntityAssociationRepository.saveAndFlush(new ArticleSearchEntityAssociation(article, searchEntity, score.total(),
                            objectMapper.convertValue(score.keywordCounter(), JsonNode.class), contextText));
                }
            } else {
                totalRejected++;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        ack.acknowledge();
        LOG.info("Received:{} Accepted:{} Rejected:{} Duplicates:{} Text loaded from Web:{} Text loaded from DB:{} Errors:{}", totalReceived, totalAccepted, totalRejected, totalDuplicates, totalTextFromWeb, totalTextFromDB, totalErrors);
    }

    private String getTitle(CrawlerRecord event, Site site) {
        String title;
        if (event.title() == null || event.title().isBlank() || event.title().isEmpty()) {
            title = String.format(noTitleTemplate, noTitleCounter++);
        } else {
            title = trimTitle(event.title(), site.getName(), site.getAcronym());
            if (title == null || title.isBlank() || title.isEmpty()) {
                title = String.format(noTitleTemplate, noTitleCounter++);
            }
        }
        return title;
    }

    private Pattern buildPattern(String name, String aliases) {
        final Pattern pattern;
        if (aliases == null) {
            pattern = Pattern.compile("([^.!?]*(" + name + ")[^.!?]*)[.!?]");
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            final String[] namesArrays = aliases.split(",");
            stringBuilder.append(name).append("|");
            for (String n : namesArrays) {
                stringBuilder.append(n).append("|");
            }
            pattern = Pattern.compile("([^.!?]*(" + stringBuilder + ")[^.!?]*)[.!?]");
        }
        return pattern;
    }

    private String extractRelevantText(String text, Pattern pattern) {
        final StringBuilder extractedText = new StringBuilder();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String sentence = matcher.group(1);
            extractedText.append(sentence).append(".");
        }
        return extractedText.toString();
    }

    private List<String> mergeNamesToList(SearchEntity searchEntity) {
        final List<String> names = new ArrayList<>();
        names.add(searchEntity.getName().toLowerCase());
        if (searchEntity.getAliases() != null) {
            String[] namesArrays = searchEntity.getAliases().split(",");
            for (String name : namesArrays) {
                names.add(name.toLowerCase());
            }
        }
        return names;
    }

    private String getText(String urlInput) {
        if (retryCounter > 2) {
            retryCounter = 0;
            return null;
        }
        final String inputTrim = urlInput.substring(0, Math.min(urlInput.length(), 240));
        rateLimiterService.increment("arquivo.pt");
        try {
            return get(inputTrim, urlInput);
        } catch (IOException e) {
            LOG.error("IOException url {}: {}", urlInput, e.getMessage());
            integrationLogRepository.save(new IntegrationLog(inputTrim, LocalDateTime.now(ZoneOffset.UTC), "processor", IntegrationLog.Status.TR, "", e.getMessage()));
            LOG.info("Will retry {}^nt attempt (max 2): {}", retryCounter, urlInput);
            try {
                Thread.sleep(500L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            try {
                retryCounter++;
                return get(inputTrim, urlInput);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private String get(String inputTrim, String urlInput) throws IOException {
        final StringBuilder everything = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(urlInput).openStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                everything.append(line);
            }
            integrationLogRepository.save(new IntegrationLog(inputTrim, LocalDateTime.now(ZoneOffset.UTC), "processor", IntegrationLog.Status.TS, "", null));
            return everything.toString();
        }
    }

    private String trimUrl(String originalUrl) {
        originalUrl = originalUrl.split("//")[1];
        if (originalUrl.contains("/amp/")) {
            originalUrl = originalUrl.replace("/amp/", "/");
        }
        return originalUrl;
    }

    private String trimTitle(String title, String siteName, String acronym) {
        boolean containsSiteOnTitle = false;
        if (title.contains("|")) {
            String[] titleParts = title.split("\\|");
            int maxLen = 0;
            int maxLenIndex = 0;
            int i = 0;
            for (String part : titleParts) {
                if (part.length() > maxLen) {
                    maxLen = part.length();
                    maxLenIndex = i;
                    i++;
                }
            }
            title = titleParts[maxLenIndex];
        }
        if (title.contains(siteName)) {
            containsSiteOnTitle = true;
            title = title.replaceAll(siteName, "");
        }
        if (acronym != null && title.contains(acronym)) {
            containsSiteOnTitle = true;
            title = title.replaceAll(acronym, "");
        }
        if (title.contains(siteName.toUpperCase())) {
            containsSiteOnTitle = true;
            title = title.replaceAll(siteName.toUpperCase(), "");
        }
        if (acronym != null && title.contains(acronym.toUpperCase())) {
            containsSiteOnTitle = true;
            title = title.replaceAll(acronym.toUpperCase(), "");
        }
        if (containsSiteOnTitle) {
            if (title.contains(" – ")) {
                title = title.replaceAll(" – ", "");
            }
            if (title.contains(" - ")) {
                title = title.replaceAll(" - ", "");
            }
            if (title.contains(" \\- ")) {
                title = title.replaceAll(" \\– ", "");
            }
            if (title.contains(" \\| ")) {
                title = title.replaceAll(" \\| ", "");
            }
        }
        if (title.startsWith(" ")) {
            title = title.replaceFirst(" ", "");
        }
        return title;
    }
}
