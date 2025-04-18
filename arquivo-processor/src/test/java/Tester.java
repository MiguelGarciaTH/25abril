import arquivo.Processor;
import arquivo.model.Article;
import arquivo.model.SearchEntity;
import arquivo.model.Site;
import arquivo.repository.ArticleRepository;
import arquivo.repository.SearchEntityRepository;
import arquivo.repository.SiteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

    @Test
    public void test() {

        StringBuilder regexp = new StringBuilder();
        regexp.append("(");
        for (String word : contextualKeywords) {
            regexp.append(word).append("|");
        }
        regexp = new StringBuilder(regexp.substring(0, regexp.length() - 1));
        regexp.append(")");
        Pattern keywordPattern = Pattern.compile(regexp.toString(), Pattern.CASE_INSENSITIVE);
        System.out.println(keywordPattern);

        String text = " A Revolução dos cravos foi muito gira";
        final Matcher matcher = keywordPattern.matcher(text);
        final long countContextualKeyword = matcher.results().count();
        System.out.println(countContextualKeyword);

    }

    @Test
    public void testTrimmedUrl() {
        String url = "https://arquivo.pt/wayback/20160904003551/https://www.publico.pt/culturaipsilon/noticia/morreu-maria-isabel-barreno-uma-das-tres-marias-1743111";
        String url2 = "https://arquivo.pt/wayback/20160904003551/https://www.publico.pt/culturaipsilon/noticia/morreu-maria-isabel-barreno-uma-das-tres-marias-1743111";
        String[] splitted = url.split("\\/\\/");
        System.out.println(splitted[2]);
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

    @Test
    void cropImage(){
        String imageUrl ="https://arquivo.pt/screenshot?url=https%3A%2F%2Farquivo.pt%2FnoFrame%2Freplay%2F20120121220334%2Fhttp%3A%2F%2Fpublico.pt%2FCultura%2Fmorreu-jose-niza-compositor-de-e-depois-do-adeus-1513320";
        URL url;
        try {
            url = new URL("https://s-media-cache-ak0.pinimg.com/236x/ac/bb/d4/acbbd49b22b8c556979418f6618a35fd.jpg");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        BufferedImage image;
        try {
            image = ImageIO.read(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedImage dest = image.getSubimage(0, 0, 1010, 659);
        File outputfile = new File("../../images/image.png");
        try {
            ImageIO.write(dest, "png", outputfile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
