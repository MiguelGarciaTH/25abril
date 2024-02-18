import arquivo.Processor;
import arquivo.model.Article;
import arquivo.model.Site;
import arquivo.repository.ArticleRepository;
import arquivo.repository.SiteRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        final Matcher matcher = pattern.matcher("teste texto António Oliveira Salazar teste texto");
        long counter = matcher.results().count();
        System.out.println(counter);

    }
}
