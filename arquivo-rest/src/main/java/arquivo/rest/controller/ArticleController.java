package arquivo.rest.controller;

import arquivo.model.Article;
import arquivo.model.SearchEntity;
import arquivo.model.Site;
import arquivo.repository.ArticleRepository;
import arquivo.repository.ArticleSearchEntityAssociationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final ArticleSearchEntityAssociationRepository articleSearchEntityAssociationRepository;

    public ArticleController(ArticleRepository articleRepository, ArticleSearchEntityAssociationRepository articleSearchEntityAssociationRepository) {
        this.articleRepository = articleRepository;
        this.articleSearchEntityAssociationRepository = articleSearchEntityAssociationRepository;
    }

    @GetMapping("/{articleId}")
    public ArticleDTO getArticle(@PathVariable int articleId) {
        final Article article = articleRepository.findById(articleId).orElse(null);
        if (article != null) {
            final ArticleDetail articleDetail = new ArticleDetail(article);
            final SearchEntityDetails searchEntityDetails = new SearchEntityDetails(article.getSearchEntities().size(), null);
            return new ArticleDTO(articleDetail, searchEntityDetails);
        }
        return null;
    }

    @GetMapping("/entity/{entityId}")
    public Page<ArticleDTO> getArticlesByEntityId(@PathVariable int entityId, Pageable pageable) {
        final Page<Article> articlePages = articleRepository.findBySearchEntityId(entityId, pageable);
        return articlePages.map(article -> new ArticleDTO(new ArticleDetail(article), null));
    }

    @GetMapping
    public Page<ArticleDTO> getArticlesBySearchTerm(@RequestParam("search_term") String searchTerm, Pageable pageable) {
        searchTerm = searchTerm.replaceAll(" " , " & ");
        final Page<Article> articlePages = articleRepository.findBySearchTerm(searchTerm, pageable);
        return articlePages.map(article -> new ArticleDTO(new ArticleDetail(article), null));
    }

    public record ArticleDTO(ArticleDetail articleDetail, SearchEntityDetails searchEntityDetails) {
        ArticleDTO(Article article) {
            this(new ArticleDetail(article), new SearchEntityDetails(article.getSearchEntities()));
        }
    }

    public record ArticleDetail(int id, String url, String title, String summary, double contextualScore, double summaryScore,
                                SiteDetail site) {
        ArticleDetail(Article article) {
            this(article.getId(), article.getUrl(), article.getTitle(), article.getSummary(), article.getContextualScore(), article.getSummaryScore(), new SiteDetail(article.getSite()));
        }
    }

    public record SiteDetail(int id, String name) {
        SiteDetail(Site site) {
            this(site.getId(), site.getName());
        }
    }

    public record SearchEntityDetails(int count, Set<SearchEntityDetail> searchEntityDetail) {
        SearchEntityDetails(Set<SearchEntity> searchEntities) {
            this(searchEntities.size(), searchEntities.stream().map(se -> new SearchEntityDetail(se.getId(), se.getName(), se.getType().name())).collect(Collectors.toSet()));
        }

    }

    public record SearchEntityDetail(int id, String name, String type) {

    }
}
