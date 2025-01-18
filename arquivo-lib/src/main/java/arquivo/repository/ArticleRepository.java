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

    @Query(nativeQuery = true, value =
            """
            select *
            from article a
            where a.summary is not null
            and a.summary_vector @@ to_tsquery('portuguese', ?1)
            """)
    Page<Article> findBySearchTerm(String searchTerm, Pageable pageable);
}
