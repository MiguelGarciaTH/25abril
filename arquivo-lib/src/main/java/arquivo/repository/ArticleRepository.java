package arquivo.repository;

import arquivo.model.Article;
import arquivo.model.ArticleSearchEntityAssociation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

    @EntityGraph(attributePaths = {"searchEntityAssociations", "site"})
    @Query(value = """
            select a
            from Article a
            where a.id = ?1
            and a.summary is not null
            """)
    Optional<Article> findByIdWithSummary(int id);

    @Query(value = """
            select a
            from Article a
            where a.id = ?1
            """)
    Optional<Article> findById(int id);

    @EntityGraph(attributePaths = {"searchEntitiesAssociations"})
    @Query(value = """
            select a
            from Article a
            where a.title = ?1
            and a.site.id = ?2
            """)
    Optional<Article> findByTitleAndSiteId(String title, int siteId);

    @Query(nativeQuery = true, value = """
            select exists(
                select 1
                from article a
                where a.title = ?1
                and a.site_id = ?2
                and a.id in (
                    select aes.article_id
                    from article_search_entity_association aes
                    where aes.search_entity_id = ?3
                )
            )
            """)
    boolean existsByTitleAndSiteAndEntityId(String title, int siteId, int entityId);

    @EntityGraph(attributePaths = {"site"})
    @Query(value = """
            select a
            from Article a
            where a.summary is not null
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
            and a.summary_score > 0
            ORDER BY a.summary_score DESC
            """)
    Page<Article> findBySearchTerm(String searchTerm, Pageable pageable);

    @EntityGraph(attributePaths = {"searchEntitiesAssociations", "site"}, type = EntityGraph.EntityGraphType.FETCH)
    @Query("""
        select a
        from Article a
        where a.contextualScore > 0
        order by a.contextualScore desc
        """)
    Page<Article> getArticleCounts(Pageable pageable);
}
