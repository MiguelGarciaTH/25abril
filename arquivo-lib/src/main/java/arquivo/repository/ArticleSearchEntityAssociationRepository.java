package arquivo.repository;

import arquivo.model.ArticleSearchEntityAssociation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleSearchEntityAssociationRepository extends JpaRepository<ArticleSearchEntityAssociation, Integer> {
    Optional<ArticleSearchEntityAssociation> findByArticleIdAndSearchEntityId(int articleId, int searchEntityId);
}
