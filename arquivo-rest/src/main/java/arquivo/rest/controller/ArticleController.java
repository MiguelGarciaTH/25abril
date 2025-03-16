package arquivo.rest.controller;

import arquivo.model.Article;
import arquivo.model.SearchEntity;
import arquivo.model.Site;
import arquivo.repository.ArticleRepository;
import arquivo.repository.ArticleSearchEntityAssociationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleRepository articleRepository;

    public ArticleController(ArticleRepository articleRepository, ArticleSearchEntityAssociationRepository articleSearchEntityAssociationRepository) {
        this.articleRepository = articleRepository;
    }

    @GetMapping("/{articleId}")
    public ArticleDTO getArticle(@PathVariable int articleId) {
        final Article article = articleRepository.findByIdWithSummary(articleId).orElse(null);
        if (article != null) {
            final ArticleDetail articleDetail = new ArticleDetail(article);
            final SearchEntityDetails searchEntityDetails = new SearchEntityDetails(article.getSearchEntities().size(), null);
            return new ArticleDTO(articleDetail, searchEntityDetails);
        }
        return null;
    }

    @GetMapping("/stats")
    public List<ArticleDTO> getArticleCounts(Pageable pageable) {
        final Page<Article> articlePages = articleRepository.getArticleCounts(pageable);
        return articlePages.map(article -> new ArticleDTO(new ArticleDetail(article), new SearchEntityDetails(article.getSearchEntities()))).getContent();
    }

    @GetMapping("/entity/{entityId}")
    public Page<ArticleDTO> getArticlesByEntityId(@PathVariable int entityId, Pageable pageable) {
        final Page<Article> articlePages = articleRepository.findBySearchEntityId(entityId, pageable);
        return articlePages.map(article -> new ArticleDTO(new ArticleDetail(article), null));
    }

    @GetMapping
    public Page<ArticleDTO> getArticlesBySearchTerm(@RequestParam("search_term") String searchTerm, Pageable pageable) {
        searchTerm = searchTerm.replaceAll(" ", " & ");
        final Page<Article> articlePages = articleRepository.findBySearchTerm(searchTerm, pageable);
        return articlePages.map(article -> new ArticleDTO(new ArticleDetail(article), new SearchEntityDetails(article.getSearchEntities())));
    }

    public record ArticleDTO(ArticleDetail articleDetail, SearchEntityDetails searchEntityDetails) {
        ArticleDTO(Article article) {
            this(new ArticleDetail(article), new SearchEntityDetails(article.getSearchEntities()));
        }
    }

    public record ArticleDetail(int id, String url, String title, String summary, double contextualScore,
                                double summaryScore, String imagePath, SiteDetail site) {
        ArticleDetail(Article article) {
            this(article.getId(), article.getUrl(), article.getTitle(), article.getSummary(), article.getContextualScore(), article.getSummaryScore(), article.getImagePath(), new SiteDetail(article.getSite()));
        }
    }

    public record SiteDetail(int id, String name) {
        SiteDetail(Site site) {
            this(site.getId(), site.getName());
        }
    }

    public record SearchEntityDetails(int count, List<SearchEntityDetail> searchEntityDetail) {
        SearchEntityDetails(List<SearchEntity> searchEntities) {
            this(searchEntities.size(), searchEntities.stream()
                    .sorted(Comparator.comparing(
                            SearchEntity::getName,
                            Collator.getInstance(new Locale("pt", "PT"))::compare
                    ))
                    .map(se -> new SearchEntityDetail(se.getId(), se.getName(), se.getType().name(), se.getImageUrl())).collect(Collectors.toList()));
        }

    }

    public record SearchEntityDetail(int id, String name, String type, String imageUrl) {

    }
}
