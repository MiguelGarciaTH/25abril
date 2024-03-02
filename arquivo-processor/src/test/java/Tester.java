import arquivo.Processor;
import arquivo.repository.ArticleRepository;
import arquivo.repository.SiteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SpringBootTest(classes = Processor.class)
@ContextConfiguration
public class Tester {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private SiteRepository siteRepository;

    private final Pattern pattern = Pattern.compile("António Oliveira Salazar", Pattern.CASE_INSENSITIVE);

    @Test
    public void test() {
        String t1 = " Xico da CUF - Observador";
        String res = trimTitle(t1, "Observador", null);
        System.out.println(res);
        assertFalse(res.contains("-"));
        assertFalse(res.startsWith(" "));

        String t2 = " Xico da CUF - Comida";
        String res2 = trimTitle(t2, "Observador", null);
        System.out.println(res2);
        assertTrue(res2.contains("-"));
        assertFalse(res2.startsWith(" "));

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
        if(containsSiteOnTitle) {
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
