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

    private static Pattern keywordPattern;

    private static final List<String> contextualKeywords = List.of(
            "revolução dos cravos",
            "revolução de abril",
            "golpe de estado",
            "golpe militar",
            "estado novo",
            "25 de abril",
            "guerra colonial",
            "ditadura",
            "censura",
            "salazarismo",
            "salazarista",
            "salazaristas",
            "clandestinos",
            "clandestinidade",
            "1974",
            "abril de 1974",
            "prisioneiros políticos"
    );

    private final Map<Integer, Pattern> namesPattern;

    public static ContextualTextScoreService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ContextualTextScoreService();
            LOG.info("A new instance of ContextualTextScoreService was created");
        }
        return INSTANCE;
    }

    public ContextualTextScoreService() {
        StringBuilder regexp = new StringBuilder();
        regexp.append("(");
        for (String word : contextualKeywords) {
            regexp.append(word).append("|");
        }
        regexp = new StringBuilder(regexp.substring(0, regexp.length() - 1));
        regexp.append(")");
        keywordPattern = Pattern.compile(regexp.toString(), Pattern.CASE_INSENSITIVE);
        namesPattern = new HashMap<>();
    }

    public Score score(String title, String url, String text, int entityId, List<String> entityNames) {
        final Map<String, Long> keywordTextCounter = new HashMap<>();

        int score = 0;

        final Matcher matcher = keywordPattern.matcher(text);
        final long countContextualKeyword = matcher.results().count();
        keywordTextCounter.put("countContextualKeyword", countContextualKeyword);
        score += countContextualKeyword;
        if (score == 0) {
            // the article is not about 25 de abril
            return new Score(0, keywordTextCounter);
        }

        if (entityNames != null) {
            final Pattern pattern = buildPattern(entityId, entityNames);
            final Matcher matcher2 = pattern.matcher(text);
            final long countNamesKeywords = matcher2.results().count();
            keywordTextCounter.put("countNamesKeywords", countNamesKeywords);
            if (countContextualKeyword > 5) {
                score += (countNamesKeywords * countContextualKeyword);
            } else {
                score += countNamesKeywords;
            }
            if (countNamesKeywords == 0) {
                // the article is not about 25 de abril
                return new Score(0, keywordTextCounter);
            }

            final Matcher matcher3 = pattern.matcher(url);
            final long countNamesUrl = matcher3.results().count();
            keywordTextCounter.put("countNamesUrl", countNamesUrl);
            score *= Math.max(1, countNamesUrl * 2);

            final Matcher matcher4 = pattern.matcher(title);
            final long countNamesTitle = matcher4.results().count();
            keywordTextCounter.put("countNamesTitle", countNamesTitle);
            score *= Math.max(1, countNamesTitle * 2);
        }
        return new Score(score, keywordTextCounter);
    }

    private Pattern buildPattern(int entityId, List<String> names) {
        if (!namesPattern.containsKey(entityId)) {
            StringBuilder regexp = new StringBuilder();
            regexp.append("(");
            for (String name : names) {
                regexp.append(name).append("|");
            }
            regexp = new StringBuilder(regexp.substring(0, regexp.length() - 1));
            regexp.append(")");
            namesPattern.put(entityId, Pattern.compile(regexp.toString(), Pattern.CASE_INSENSITIVE));
        }
        return namesPattern.get(entityId);
    }

    public record Score(int total, Map<String, Long> keywordCounter) {

    }
}
