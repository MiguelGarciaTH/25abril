package arquivo.services;

import arquivo.model.SearchEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextScoreService {

    private static TextScoreService INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(TextScoreService.class);

    private static Pattern keywordPattern;

    private final Map<Integer, Pattern> namesPattern;

    public static TextScoreService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TextScoreService();
            LOG.info("A new instance of ContextualTextScoreService was created");
        }
        return INSTANCE;
    }

    public TextScoreService() {
        StringBuilder regexp = new StringBuilder();
        regexp.append("\\b("); // opening word boundary and group
        for (String word : Keywords.contextualKeywords) {
            regexp.append(Pattern.quote(word)).append("|"); // quote each word to escape special characters
        }
        regexp.setLength(regexp.length() - 1); // remove the last '|'
        regexp.append(")\\b"); // closing group and word boundary
        keywordPattern = Pattern.compile(regexp.toString(), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        namesPattern = new HashMap<>();
    }

    public Score textScore(String title, String url, String text, Pattern pattern, boolean scoreTextScaling, String counterPrefix, int counterScoreWeight) {
        long score = 0;
        final Map<String, Long> counterMap = new HashMap<>();

        final Matcher keywordMatcher = pattern.matcher(text);
        final long textCount = keywordMatcher.results().count();
        counterMap.put(counterPrefix + "TextCount", textCount);
        if (textCount == 0) {
            // the article is not about 25 de abril or search entity
            return new Score(0, counterMap);
        }

        score += scoreTextScaling ? textCount / text.length() : textCount;

        final Matcher urlMatcher = pattern.matcher(url);
        final long urlCount = urlMatcher.results().count();
        counterMap.put(counterPrefix + "UrlCount", urlCount);
        score += Math.max(1, urlCount * counterScoreWeight);

        final Matcher titleMatcher = pattern.matcher(title);
        final long textCounter = titleMatcher.results().count();
        counterMap.put(counterPrefix + "TitleCount", textCounter);
        score += Math.max(1, textCounter * counterScoreWeight);

        return new Score(BigDecimal.valueOf(score).setScale(2, RoundingMode.CEILING).doubleValue(), counterMap);
    }

    public Pattern getKeywordPattern() {
        return keywordPattern;
    }

    public Pattern getNamePattern(SearchEntity searchEntity) {
        if (!namesPattern.containsKey(searchEntity.getId())) {
            final List<String> names = mergeNamesToList(searchEntity);
            StringBuilder regexp = new StringBuilder();
            regexp.append("(");
            for (String name : names) {
                regexp.append(name).append("|");
            }
            regexp = new StringBuilder(regexp.substring(0, regexp.length() - 1));
            regexp.append(")");
            namesPattern.put(searchEntity.getId(), Pattern.compile(regexp.toString(), Pattern.CASE_INSENSITIVE));
        }
        return namesPattern.get(searchEntity.getId());
    }

    public Score searchEntityscore(String text, SearchEntity searchEntity) {
        final Map<String, Long> keywordTextCounter = new HashMap<>();

        final Pattern pattern = getNamePattern(searchEntity);

        final Matcher namesMatcher = pattern.matcher(text);
        final long countNamesKeywords = namesMatcher.results().count();
        keywordTextCounter.put("countNamesKeywords", countNamesKeywords);
        if (countNamesKeywords == 0) {
            // the article is not about the search entity
            return new Score(0, keywordTextCounter);
        }

        return new Score(BigDecimal.valueOf(countNamesKeywords * 100).setScale(2, RoundingMode.CEILING).doubleValue(), keywordTextCounter);
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

    @JsonSerialize
    public record Score(double total, Map<String, Long> keywordCounter) {

    }
}
