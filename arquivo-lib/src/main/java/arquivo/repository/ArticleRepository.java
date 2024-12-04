package arquivo.repository;

import arquivo.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

    @Query(nativeQuery = true, value = """
            select *
            from article a
            where a.trimmed_url = ?1
            and a.site_id = ?2
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
}
