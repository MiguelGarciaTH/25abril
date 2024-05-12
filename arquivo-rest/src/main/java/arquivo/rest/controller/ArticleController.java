package arquivo.rest.controller;

import arquivo.model.ArticleRecord;
import arquivo.model.SearchEntity;
import arquivo.repository.ArticleRepository;
import arquivo.repository.SearchEntityRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final SearchEntityRepository searchEntityRepository;
    private final Pageable topTwenty;

    ArticleController(ArticleRepository articleRepository, SearchEntityRepository searchEntityRepository) {
        this.articleRepository = articleRepository;
        this.searchEntityRepository = searchEntityRepository;
        this.topTwenty = PageRequest.of(0, 200);
    }

    @GetMapping("/{entityId}")
    List<ArticleRecordSentences> getArticleBySearchEntity(@PathVariable int entityId) {
        final SearchEntity searchEntity = searchEntityRepository.findById(entityId).orElse(null);
        final List<ArticleRecord> records = articleRepository.findByAllBySearchEntityId(entityId, topTwenty);
        final List<ArticleRecordSentences> recordSentences = new ArrayList<>();
        for (ArticleRecord record : records) {
            recordSentences.add(new ArticleRecordSentences(record.articleId(), record.entityId(), searchEntity.getName(), record.siteId(), record.siteName(), record.score(), record.url(), record.title(), record.text()));
        }
        return recordSentences;
    }

    @GetMapping("/{entityId}/{articleId}")
    ArticleRecordSentences getArticleBySearchEntity(@PathVariable int entityId, @PathVariable int articleId) {
        final SearchEntity searchEntity = searchEntityRepository.findById(entityId).orElse(null);
        final ArticleRecord record = articleRepository.findByAllBySearchEntityIdAndArticleId(entityId, articleId);
        return new ArticleRecordSentences(record.articleId(), record.entityId(), searchEntity.getName(), record.siteId(), record.siteName(), record.score(), record.url(), record.title(), record.text());
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
                                  String texts) {
    }
}
