package arquivo.repository;

import arquivo.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

    @EntityGraph(attributePaths = {"searchEntities", "site"})
    @Query(value = """
            select a
            from Article a
            where a.id = ?1
            and a.summary is not null
            """)
    Optional<Article> findById(int id);

    @EntityGraph(value = "Article.searchEntities")
    @Query(value = """
            select a
            from Article a
            where a.trimmedUrl = ?1
            and a.site.id = ?2
            """)
    Optional<Article> findByTrimmedUrlAndSiteId(String originalUrl, int siteId);

    @Query(nativeQuery = true, value = """
            select exists(
                select 1
                from article a
                where a.trimmed_url = ?1
                and a.site_id = ?2
                and a.id in (
                    select aes.article_id
                    from article_search_entity_association aes
                    where aes.search_entity_id = ?3
                )
            )
            """)
    boolean existsByTrimmedUrlAndSiteAndEntityId(String trimmedUrl, int siteId, int entityId);

    @EntityGraph(attributePaths = {"searchEntities", "site"})
    @Query(value = """
            select a
            from Article a
            join a.searchEntities se
            where a.summary is not null
            and se.id =?1
            """)
    Page<Article> findBySearchEntityId(int entityId, Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT a.*
            FROM article a
            WHERE a.summary IS NOT NULL
            AND (
                a.summary_vector @@ to_tsquery('portuguese', ?1)
                or
                a.id in (
                    select distinct(aes.article_id)
                    from article_search_entity_association aes
                    inner join search_entity se on se.id = aes.search_entity_id
                    where se.names_vector @@ to_tsquery('portuguese', ?1)
                )
            )
            ORDER BY ((a.contextual_score * 100) + a.summary_score * 10) DESC
            """)
    Page<Article> findBySearchTerm(String searchTerm, Pageable pageable);
}
