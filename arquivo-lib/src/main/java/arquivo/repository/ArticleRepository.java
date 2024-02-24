package arquivo.repository;

import arquivo.model.Article;
import arquivo.model.ArticleRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

    //Integer articleId, Integer siteId, Long score, String url, String title
    @Query(value = """
            select new arquivo.model.ArticleRecord(a.id, a.site.id, asa.score, a.url, a.title) 
            from ArticleSearchEntityAssociation asa
            inner join Article a on a.id = asa.article.id
            where asa.searchEntity.id = ?1
            order by asa.score desc
            """)
    List<ArticleRecord> findByAllBySearchEntityId(int entityId);

    @Query(nativeQuery = true, value = """
            select count(aes.article_id) 
            from article_search_entity_association aes
            where aes.search_entity_id = ?1
            """)
    int countByAllBySearchEntityId(int entityId);

    Optional<Article> findByOriginalUrl(String originalUrl);
}
