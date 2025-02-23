package arquivo.services;

import arquivo.model.SearchEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        regexp.append("(");
        for (String word : Keywords.contextualKeywords) {
            regexp.append(word).append("|");
        }
        regexp = new StringBuilder(regexp.substring(0, regexp.length() - 1));
        regexp.append(")");
        keywordPattern = Pattern.compile(regexp.toString(), Pattern.CASE_INSENSITIVE);
        namesPattern = new HashMap<>();
    }

    public Score contextualScore(String title, String url, String text) {
        long score = 0;
        final Map<String, Long> keywordTextCounter = new HashMap<>();

        final Matcher keywordMatcher = keywordPattern.matcher(text);
        final long countContextualKeyword = keywordMatcher.results().count();
        keywordTextCounter.put("countContextualKeyword", countContextualKeyword);
        if (countContextualKeyword == 0) {
            // the article is not about 25 de abril
            return new Score(0, keywordTextCounter);
        }
        score += countContextualKeyword;

        final Matcher urlMatcher = keywordPattern.matcher(url);
        final long countNamesUrl = urlMatcher.results().count();
        keywordTextCounter.put("countContextualUrl", countNamesUrl);
        score *= Math.max(1, countNamesUrl * 2);

        final Matcher titleMatcher = keywordPattern.matcher(title);
        final long countNamesTitle = titleMatcher.results().count();
        keywordTextCounter.put("countContextuaTitle", countNamesTitle);
        score *= Math.max(1, countNamesTitle * 2);

        return new Score((double) score / text.length(), keywordTextCounter);
    }

    public Score searchEntityscore(String title, String url, String text, SearchEntity searchEntity) {
        long score = 0;
        final Map<String, Long> keywordTextCounter = new HashMap<>();

        final Pattern pattern = getNamePattern(searchEntity);

        final Matcher namesMatcher = pattern.matcher(text);
        final long countNamesKeywords = namesMatcher.results().count();
        keywordTextCounter.put("countNamesKeywords", countNamesKeywords);
        if (countNamesKeywords == 0) {
            // the article is not about the search entity
            return new Score(0, keywordTextCounter);
        }
        score += countNamesKeywords;

        final Matcher urlMatcher = pattern.matcher(url);
        final long countNamesUrl = urlMatcher.results().count();
        keywordTextCounter.put("countNamesUrl", countNamesUrl);
        score *= Math.max(1, countNamesUrl * 2);

        final Matcher titleMatcher = pattern.matcher(title);
        final long countNamesTitle = titleMatcher.results().count();
        keywordTextCounter.put("countNamesTitle", countNamesTitle);
        score *= Math.max(1, countNamesTitle * 2);

        return new Score((double) score / text.length(), keywordTextCounter);
    }

    private Pattern getNamePattern(SearchEntity searchEntity) {
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
