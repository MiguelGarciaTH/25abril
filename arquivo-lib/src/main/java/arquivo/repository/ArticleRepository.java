package arquivo.repository;

import arquivo.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

    @Query(value = """
            select a
            from Article a
            where a.id = ?1
            """)
    Optional<Article> findById(int id);

    @Query(value = """
            select a
            from Article a
            where a.title = ?1
            and a.site.id = ?2
            """)
    Optional<Article> findByTitleAndSiteId(String title, int siteId);

    @Query(nativeQuery = true, value = """
            SELECT EXISTS (
                SELECT 1
                FROM article a
                JOIN article_search_entity_association aes ON aes.article_id = a.id
                WHERE a.title = ?1
                  AND a.site_id = ?2
                  AND aes.search_entity_id = ?3
            )
            """)
    boolean existsByTitleAndSiteAndEntityId(String title, int siteId, int entityId);

    @Query(value = """
            SELECT a
            FROM Article a
            JOIN a.site s
            JOIN ArticleSearchEntityAssociation assoc ON assoc.article.id = a.id
            WHERE assoc.searchEntity.id = :searchEntityId
              AND a.summary IS NOT NULL
              AND a.summaryScore > 0.0
            ORDER BY assoc.entityScore, a.summaryScore DESC
            """)
    Page<Article> findBySearchEntityId(int searchEntityId, Pageable pageable);

    @Query(value = """
            select a
            from Article a
            join a.site s
            where s.id = ?1
            and a.summary is not null
            and a.summaryScore > 0.0
            ORDER BY a.summaryScore desc
            """)
    Page<Article> findBySiteId(int siteId, Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT a.*
            FROM article a
            WHERE a.summary IS NOT NULL
              AND a.summary_score > 0
              AND (
                a.summary_vector @@ to_tsquery('portuguese', ?1)
                OR EXISTS (
                  SELECT 1
                  FROM article_search_entity_association aes
                  JOIN search_entity se ON se.id = aes.search_entity_id
                  WHERE aes.article_id = a.id
                    AND se.names_vector @@ to_tsquery('portuguese', ?1)
                )
              )
            ORDER BY a.summary_score DESC
            """)
    Page<Article> findBySearchTerm(String searchTerm, Pageable pageable);

    @Query("""
            select a
            from Article a
            join a.site s
            where a.summaryScore > 0
            order by a.summaryScore desc
            """)
    Page<Article> getArticleCountsByRelevance(Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT a.*
            FROM article a
            LEFT JOIN article_search_entity_association asea ON a.id = asea.article_id
            WHERE a.summary_score > 0
            GROUP BY a.id
            ORDER BY COUNT(asea.search_entity_id) DESC, a.summary_score DESC
            """)
    Page<Article> getArticleCountsByEntities(Pageable pageable);
}
