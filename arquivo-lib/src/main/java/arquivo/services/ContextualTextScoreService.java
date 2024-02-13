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
            Map.entry("revolução dos cravos", 10),
            Map.entry("estado novo", 10),
            Map.entry("25 de abril", 10),
            Map.entry("revolução abril", 10),
            Map.entry("guerra colonial", 10),
            Map.entry("salazarismo", 10),
            Map.entry("salazarista", 10),
            Map.entry("salazaristas", 10),
            Map.entry("clandestinidade", 5),
            Map.entry("1974", 5),
            Map.entry("abril de 1974", 5)
    );

    private Map<String, Pattern> keywordsPatternMap;
    private Map<Integer, Map<String, Pattern>> searchEntityMapOfPatternMaps;

    public static ContextualTextScoreService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ContextualTextScoreService();
            LOG.info("A new instance of ContextualTextScoreService was created");
        }
        return INSTANCE;
    }

    public ContextualTextScoreService() {
        keywordsPatternMap = new HashMap<>();
        searchEntityMapOfPatternMaps = new HashMap<>();
        for (String key : keywordsScoreMap.keySet()) {
            Pattern pattern = Pattern.compile(key);
            keywordsPatternMap.put(key, pattern);
        }
        for(var entry : keywordsPatternMap.entrySet()){
            System.out.println("key = " + entry.getKey() + " Value = " + entry.getValue().toString());
        }
    }

    public Score score(String text, int entityId, List<String> entityNames) {
        int score = 0;

        // score by contextual keywords
        final Map<String, Long> keywordCounter = getKeywordCounter(text);
        for (var entry : keywordCounter.entrySet()) {
            score += entry.getValue() * keywordsScoreMap.get(entry.getKey());
        }

        // score by entity names
        if (entityNames != null) {
            Map<String, Long> entityNamesCounter = getEntityNamesCounter(text, entityId, entityNames);
            for (var entry : entityNamesCounter.entrySet()) {
                for (var entry2 : keywordCounter.entrySet()) {
                    // multiple the entity in the text by the contextual scores raising exponentially the score for real articles
                    score += entry.getValue() * entry2.getValue();
                }
            }
            // merge both counter maps
            keywordCounter.putAll(entityNamesCounter);
        }
        return new Score(score, keywordCounter);
    }

    private Map<String, Long> getKeywordCounter(String text) {
        final Map<String, Long> keywordCounter = new HashMap<>();
        for (var keyword : keywordsScoreMap.entrySet()) {
            final Pattern pattern = keywordsPatternMap.get(keyword.getKey());
            final Matcher matcher = pattern.matcher(text);
            long counter = matcher.results().count();
            keywordCounter.put(keyword.getKey(), counter);
            LOG.debug("Keyword={} Count={}", keyword.getKey(), counter);
        }
        return keywordCounter;
    }

    private Map<String, Long> getEntityNamesCounter(String text, int entityId, List<String> entityNames) {
        final Map<String, Long> keywordCounter = new HashMap<>();
        Map<String, Pattern> searchEntityTitlePatternMap;
        if (searchEntityMapOfPatternMaps.containsKey(entityId)) {
            searchEntityTitlePatternMap = searchEntityMapOfPatternMaps.get(entityId);
        } else {
            searchEntityTitlePatternMap = new HashMap<>();
            for (String key : entityNames) {
                searchEntityTitlePatternMap.put(key, Pattern.compile(key));
            }
            searchEntityMapOfPatternMaps.put(entityId, searchEntityTitlePatternMap);
        }
        for (String name : entityNames) {
            final Pattern pattern = searchEntityTitlePatternMap.get(name);
            final Matcher matcher = pattern.matcher(text);
            long counter = matcher.results().count();
            keywordCounter.put(name, counter);
            LOG.debug("Keyword={} Count={}", name, counter);
        }
        return keywordCounter;
    }

    public record Score(int total, Map<String, Long> keywordCounter) {

    }
}
