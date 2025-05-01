package arquivo.rest.controller;

import arquivo.model.Article;
import arquivo.model.SearchEntity;
import arquivo.model.Site;
import arquivo.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.text.Collator;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleRepository articleRepository;

    public ArticleController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    private static final Pattern DIACRITICS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public static String normalizeSearchTerm(String input) {
        if (input == null) return "";

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        String withoutAccents = DIACRITICS.matcher(normalized).replaceAll("");

        return withoutAccents.toLowerCase().trim();
    }

    @GetMapping("/entity/{entityId}")
    public Page<ArticleDTO> getArticlesByEntityId(@PathVariable int entityId, Pageable pageable) {
        final Page<Article> articlePages = articleRepository.findBySearchEntityId(entityId, pageable);
        return articlePages.map(article -> new ArticleDTO(new ArticleDetail(article), null));
    }

    @GetMapping("/site/{siteId}")
    public Page<ArticleDTO> getArticlesBySiteId(@PathVariable int siteId, Pageable pageable) {
        final Page<Article> articlePages = articleRepository.findBySiteId(siteId, pageable);
        return articlePages.map(article -> new ArticleDTO(new ArticleDetail(article), null));
    }

    @GetMapping("/type/{type}")
    public Page<ArticleDTO> getArticlesBySiteId(@PathVariable String type, Pageable pageable) {
        final Page<Article> articlePages = articleRepository.findBySearchEntityType(SearchEntity.Type.valueOf(type), pageable);
        return articlePages.map(article -> new ArticleDTO(new ArticleDetail(article), null));
    }

    @GetMapping("/stats/top-relevance")
    public List<ArticleDTO> getArticleCountsByRelevance(Pageable pageable) {
        final Page<Article> articlePages = articleRepository.getArticleCountsByRelevance(pageable);
        return articlePages.map(article -> new ArticleDTO(new ArticleDetail(article), new SearchEntityDetails(article.getSearchEntities()))).getContent();
    }

    @GetMapping("/stats/top-entities")
    public List<ArticleDTO> getArticleCountsByEntities(Pageable pageable) {
        final Page<Article> articlePages = articleRepository.getArticleCountsByEntities(pageable);
        return articlePages.map(article -> new ArticleDTO(new ArticleDetail(article), new SearchEntityDetails(article.getSearchEntities()))).getContent();
    }

    @GetMapping
    public Page<ArticleDTO> getArticlesBySearchTerm(@RequestParam("search_term") String searchTerm, Pageable pageable) {
        final String normalized = normalizeSearchTerm(searchTerm);
        final String query = Arrays.stream(normalized.split("\\s+"))
                .map(word -> word + ":*")
                .collect(Collectors.joining(" & "));
        final Page<Article> articlePages = articleRepository.findBySearchTerm(query, pageable);
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
