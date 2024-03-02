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
        final SearchEntity entity = searchEntityRepository.findByName("José Afonso");
        Pattern p;
        if (entity.getAliases() == null) {
            p = Pattern.compile("([A-Z][^.?!]*?)?(?<!\\w)(?i)(" + entity.getName() + ")(?!\\w)[^.?!]*?[.?!]{1,2}\"?");
        } else {
            String[] namesArrays = entity.getAliases().split(",");
            String pattern = entity.getName() + "|";
            for (String name : namesArrays) {
                pattern += name + "|";
            }
            p = Pattern.compile("([A-Z][^.?!]*?)?(?<!\\w)(?i)(" + pattern + ")(?!\\w)[^.?!]*?[.?!]{1,2}\"?");
        }
        final List<ArticleRecord> articles = articleRepository.findByAllBySearchEntityId(entity.getId());
        int i = 0;
        for (ArticleRecord article : articles) {
           // List<String> texts = getRelevantSentences(article.text(), p);

        }
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
