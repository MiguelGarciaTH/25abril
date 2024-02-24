package arquivo.rest.controller;

import arquivo.model.ArticleRecord;
import arquivo.model.ArticleSearchEntityAssociation;
import arquivo.repository.ArticleRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {

    private final ArticleRepository articleRepository;

    ArticleController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @GetMapping("/{entityId}")
    List<ArticleRecord> getSearchEntity(@PathVariable int entityId) {
        return articleRepository.findByAllBySearchEntityId(entityId);
    }

    @GetMapping("/count/{entityId}")
    Integer countArticlesBySearchEntity(@PathVariable int entityId) {
        return articleRepository.countByAllBySearchEntityId(entityId);
    }
}
