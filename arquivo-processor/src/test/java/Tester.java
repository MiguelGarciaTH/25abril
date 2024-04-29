import arquivo.Processor;
import arquivo.model.ArticleRecord;
import arquivo.model.SearchEntity;
import arquivo.repository.ArticleRepository;
import arquivo.repository.SearchEntityRepository;
import arquivo.repository.SiteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest(classes = Processor.class)
@ContextConfiguration
public class Tester {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private SearchEntityRepository searchEntityRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Test
    public void test() {

        String site1 = "https://arquivo.pt/wayback/20010420014947/http://www.expresso.pt/ed1464/r1501.asp";
        String site2 = "https://arquivo.pt/wayback/20010420014947/http://www.expresso.pt/ed1464/r1501.asp?il";
        final Map<String, Integer> keywordsScoreMap = Map.ofEntries(
                Map.entry("revolução dos cravos", 2),
                //Map.entry("estado novo", 2),
                //Map.entry("25 de abril", 2),
                //Map.entry("revolução abril", 2),
                //Map.entry("guerra colonial", 2),
                //Map.entry("salazarismo", 2),
                Map.entry("salazarista", 2),
                //Map.entry("salazaristas", 2),
                //Map.entry("clandestinidade", 1),
                //Map.entry("1974", 1),
                Map.entry("abril de 1974", 2));

        StringBuilder regexp = new StringBuilder();
        regexp.append("(");
        for (String word : keywordsScoreMap.keySet()) {
            regexp.append(word).append("|");
        }
        regexp = new StringBuilder(regexp.substring(0, regexp.length()-1));
        regexp.append(")");
        System.out.println(regexp.toString());
        Pattern keywordPattern = Pattern.compile(regexp.toString());

        String text = "Em abril de 1974 derrobou-se o regime salazarista com a revolução dos cravos";
        Matcher matcher = keywordPattern.matcher(text);
        long counter = matcher.results().count();
        System.out.println(counter);
    }


    private List<String> getRelevantSentences(String text, String name, String aliases) {
        final Pattern pattern;
        if (aliases == null) {
            pattern = Pattern.compile("([A-Z][^.?!]*?)?(?<!\\w)(?i)(" + name + ")(?!\\w)[^.?!]*?[.?!]{1,2}\"?");
        } else {
            final String[] namesArrays = aliases.split(",");
            String patternStr = name + "|";
            for (String n : namesArrays) {
                patternStr += n + "|";
            }
            pattern = Pattern.compile("([A-Z][^.?!]*?)?(?<!\\w)(?i)(" + patternStr + ")(?!\\w)[^.?!]*?[.?!]{1,2}\"?");
        }

        final Matcher match = pattern.matcher(text);
        final List<String> sentences = new ArrayList<>();
        while (match.find()) {
            sentences.add(match.group(0));
        }
        return sentences;
    }

    private String trimUrl(String originalUrl) {
        originalUrl = originalUrl.split("//")[2];
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
