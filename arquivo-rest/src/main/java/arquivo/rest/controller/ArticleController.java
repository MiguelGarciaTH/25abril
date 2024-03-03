package arquivo.rest.controller;

import arquivo.model.ArticleRecord;
import arquivo.model.SearchEntity;
import arquivo.repository.ArticleRepository;
import arquivo.repository.SearchEntityRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final SearchEntityRepository searchEntityRepository;

    ArticleController(ArticleRepository articleRepository, SearchEntityRepository searchEntityRepository) {
        this.articleRepository = articleRepository;
        this.searchEntityRepository = searchEntityRepository;
    }

    @GetMapping("/{entityId}")
    List<ArticleRecordSentences> getArticleBySearchEntity(@PathVariable int entityId) {
        final SearchEntity searchEntity = searchEntityRepository.findById(entityId).orElse(null);
        final Pattern pattern = buildPattern(searchEntity.getName(), searchEntity.getAliases());
        final List<ArticleRecord> records = articleRepository.findByAllBySearchEntityId(entityId);
        final List<ArticleRecordSentences> recordSentences = new ArrayList<>();
        for (ArticleRecord record : records) {
            final List<String> texts = getRelevantSentences(record.text(), pattern);
            recordSentences.add(new ArticleRecordSentences(record.articleId(), record.entityId(), searchEntity.getName(), record.siteId(), record.siteName(), record.score(), record.url(), record.title(), texts));
        }
        return recordSentences;
    }

    @GetMapping("/{entityId}/{articleId}")
    ArticleRecordSentences getArticleBySearchEntity(@PathVariable int entityId, @PathVariable int articleId) {
        final SearchEntity searchEntity = searchEntityRepository.findById(entityId).orElse(null);
        final Pattern pattern = buildPattern(searchEntity.getName(), searchEntity.getAliases());
        final ArticleRecord record = articleRepository.findByAllBySearchEntityIdAndArticleId(entityId, articleId);
        final List<String> texts = getRelevantSentences(record.text(), pattern);
        return new ArticleRecordSentences(record.articleId(), record.entityId(), searchEntity.getName(), record.siteId(), record.siteName(), record.score(), record.url(), record.title(), texts);
    }

    @GetMapping("/{articleId}/entities")
    List<SearchEntity> getSearchEntityByArticle(@PathVariable int articleId) {
        return articleRepository.findByAllByArticleId(articleId);
    }

    @GetMapping("/count/{entityId}")
    Integer countArticlesBySearchEntity(@PathVariable int entityId) {
        return articleRepository.countByAllBySearchEntityId(entityId);
    }

    record ArticleRecordSentences(int articleId, int entityId, String entityName, int siteId, String siteName, int score, String url,
                                  String title,
                                  List<String> texts) {
    }

    private Pattern buildPattern(String name, String aliases) {
        final Pattern pattern;
        if (aliases == null) {
            pattern = Pattern.compile("([A-Z][^.?!]*?)?(?<!\\w)(?i)(" + name + ")(?!\\w)[^.?!]*?[.?!]{1,2}\"?");
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            final String[] namesArrays = aliases.split(",");
            stringBuilder.append(name).append("|");
            for (String n : namesArrays) {
                stringBuilder.append(n).append("|");
            }
            pattern = Pattern.compile("([A-Z][^.?!]*?)?(?<!\\w)(?i)(" + stringBuilder + ")(?!\\w)[^.?!]*?[.?!]{1,2}\"?");
        }
        return pattern;
    }

    private List<String> getRelevantSentences(String text, Pattern pattern) {
        final Matcher match = pattern.matcher(text);
        final List<String> sentences = new ArrayList<>();
        while (match.find()) {
            sentences.add(match.group(0));
        }
        return sentences;
    }
}
