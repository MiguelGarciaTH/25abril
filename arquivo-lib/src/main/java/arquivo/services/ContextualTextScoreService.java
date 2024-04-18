package arquivo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContextualTextScoreService {

    private static ContextualTextScoreService INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(ContextualTextScoreService.class);

    private static final Map<String, Integer> keywordsScoreMap = Map.ofEntries(
            Map.entry("revolução dos cravos", 2),
            Map.entry("estado novo", 2),
            Map.entry("25 de abril", 2),
            Map.entry("revolução abril", 2),
            Map.entry("guerra colonial", 2),
            Map.entry("salazarismo", 2),
            Map.entry("salazarista", 2),
            Map.entry("salazaristas", 2),
            Map.entry("clandestinidade", 1),
            Map.entry("1974", 1),
            Map.entry("abril de 1974", 2)
    );

    private final Map<String, Pattern> keywordsPatternMap;
    private final Map<Integer, Map<String, Pattern>> searchEntityMapOfPatternMaps;

    public static ContextualTextScoreService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ContextualTextScoreService();
            LOG.info("A new instance of ContextualTextScoreService was created");
        }
        return INSTANCE;
    }

    public ContextualTextScoreService() {
        searchEntityMapOfPatternMaps = new HashMap<>();
        keywordsPatternMap = new HashMap<>();
        for (String key : keywordsScoreMap.keySet()) {
            Pattern pattern = Pattern.compile(key, Pattern.CASE_INSENSITIVE);
            keywordsPatternMap.put(key, pattern);
        }
    }

    public Score score(String title, String url, String text, int entityId, List<String> entityNames) {
        int score = 0;

        // score by contextual keywords
        final Map<String, Long> keywordTextCounter = getKeywordTextCounter(text);
        // remove to avoid non entity articles being score
        /*for (var entry : keywordTextCounter.entrySet()) {
            score += entry.getValue() * keywordsScoreMap.get(entry.getKey());
        }*/

        // score by entity names
        if (entityNames != null) {
            final Map<String, Long> entityNamesTextCounter = getEntityNamesTextCounter(text, entityId, entityNames);
            for (var entry : entityNamesTextCounter.entrySet()) {
                for (var entry2 : keywordTextCounter.entrySet()) {
                    // multiple the entity in the text by the contextual scores raising exponentially the score for real articles
                    score += (entry.getValue() * entry2.getValue());
                }
            }
            // merge both counter maps
            keywordTextCounter.putAll(entityNamesTextCounter);

            if (title != null && url != null) {
                final Map<String, Long> entityNamesUrlAndTitleCounter = getEntityNameTitleAndUrlCounter(entityId, title, url, entityNames);
                for (var entry : entityNamesUrlAndTitleCounter.entrySet()) {
                    score += (entry.getValue() * 2);
                }
                keywordTextCounter.putAll(entityNamesUrlAndTitleCounter);
            }
        }
        return new Score(score, keywordTextCounter);
    }

    private Map<String, Long> getKeywordTextCounter(String text) {
        final Map<String, Long> keywordCounter = new HashMap<>();
        for (var keyword : keywordsScoreMap.entrySet()) {
            final Pattern pattern = keywordsPatternMap.get(keyword.getKey());
            final Matcher matcher = pattern.matcher(text);
            long counter = matcher.results().count();
            keywordCounter.put(keyword.getKey(), counter);
            LOG.debug("Text keywords: Keyword={} Count={}", keyword.getKey(), counter);
        }
        return keywordCounter;
    }

    private Map<String, Long> getEntityNamesTextCounter(String text, int entityId, List<String> entityNames) {
        final Map<String, Long> keywordCounter = new HashMap<>();
        Map<String, Pattern> searchEntityNamePatternMap;
        if (searchEntityMapOfPatternMaps.containsKey(entityId)) {
            searchEntityNamePatternMap = searchEntityMapOfPatternMaps.get(entityId);
        } else {
            searchEntityNamePatternMap = new HashMap<>();
            for (String key : entityNames) {
                searchEntityNamePatternMap.put(key, Pattern.compile(key, Pattern.CASE_INSENSITIVE));
            }
            searchEntityMapOfPatternMaps.put(entityId, searchEntityNamePatternMap);
        }
        for (String name : entityNames) {
            final Pattern pattern = searchEntityNamePatternMap.get(name);
            final Matcher matcher = pattern.matcher(text);
            long counter = matcher.results().count();
            keywordCounter.put(name + "(text)", counter);
            LOG.debug("Text entity name: Keyword={} Count={}", name, counter);
        }
        return keywordCounter;
    }

    private Map<String, Long> getEntityNameTitleAndUrlCounter(int entityId, String title, String url, List<String> entityNames) {
        final Map<String, Pattern> searchEntityNamePatternMap = searchEntityMapOfPatternMaps.get(entityId);
        final Map<String, Long> keywordCounter = new HashMap<>();
        for (String name : entityNames) {
            final Pattern pattern = searchEntityNamePatternMap.get(name);
            final Matcher matcher = pattern.matcher(title);
            final Matcher matcher2 = pattern.matcher(url.replaceAll("-", " "));
            long counter = (matcher.results().count() + matcher2.results().count());
            keywordCounter.put(name + "(title)", counter);
            LOG.debug("Text title and url: Keyword={} Count={} url={}", name, counter, url);
        }
        return keywordCounter;
    }

    public record Score(int total, Map<String, Long> keywordCounter) {

    }
}
