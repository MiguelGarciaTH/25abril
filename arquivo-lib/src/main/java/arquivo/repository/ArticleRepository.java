package arquivo.repository;

import arquivo.model.Article;
import arquivo.model.ArticleRecord;
import arquivo.model.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

    //Integer articleId, Integer siteId, Long score, String url, String title
    @Query(value = """
            select new arquivo.model.ArticleRecord(a.id, a.site.id, s.name, asa.score, a.url, a.title, a.text) 
            from ArticleSearchEntityAssociation asa
            inner join Article a on a.id = asa.article.id
            inner join Site s on s.id = a.site.id
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

    @Query(nativeQuery = true, value = """
            select *
            from article a
            where a.original_url like CONCAT('%', ?1, '%')
            """)
    Optional<Article> findByOriginalUrl(String originalUrl);

    @Query("""
            select se
            from ArticleSearchEntityAssociation asa
            inner join SearchEntity se on se.id = asa.searchEntity.id
            where asa.article.id = ?1
            order by se.name asc
            """)
    List<SearchEntity> findByAllByArticleId(int articleId);
}
