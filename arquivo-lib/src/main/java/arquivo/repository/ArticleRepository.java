package arquivo.repository;

import arquivo.model.Article;
import arquivo.model.ArticleRecord;
import arquivo.model.SearchEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Integer> {

    //Integer articleId, Integer siteId, Long score, String url, String title
    @Query(value = """
            select new arquivo.model.ArticleRecord(a.id, asa.searchEntity.id, a.site.id, s.name, asa.score, a.url, a.title)
            from ArticleSearchEntityAssociation asa
            inner join Article a on a.id = asa.article.id
            inner join Site s on s.id = a.site.id
            where asa.searchEntity.id = ?1
            order by asa.score desc
            """)
    List<ArticleRecord> findByAllBySearchEntityId(int entityId, Pageable topTwenty);

    @Query(value = """
            select new arquivo.model.ArticleRecord(a.id, asa.searchEntity.id, a.site.id, s.name, asa.score, a.url, a.title)
            from ArticleSearchEntityAssociation asa
            inner join Article a on a.id = asa.article.id
            inner join Site s on s.id = a.site.id
            where asa.searchEntity.id = ?1
            and asa.article.id = ?2
            order by asa.score desc
            """)
    ArticleRecord findByAllBySearchEntityIdAndArticleId(int entityId, int articleId);


    @Query(nativeQuery = true, value = """
            select count(aes.article_id)
            from article_search_entity_association aes
            where aes.search_entity_id = ?1
            """)
    int countByAllBySearchEntityId(int entityId);

    @Query(nativeQuery = true, value = """
            select *
            from article a
            where a.url = ?1
            and a.site_id = ?2
            """)
    Optional<Article> findByOriginalUrl(String originalUrl, int siteId);

    @Query(nativeQuery = true, value = """
            select exists(
                select 1
                from article a
                where a.url = ?1
                and a.site_id = ?2
            )
            """)
    boolean existsByTitleAndSite(String title, int siteId);

    @Query(nativeQuery = true, value = """
            select exists(
                select 1
                from article a
                where a.url = ?1
                and a.site_id = ?2
                and a.id in (
                    select aes.article_id
                    from article_search_entity_association aes
                    where aes.search_entity_id = ?3
                )
            )
            """)
    boolean existsByTitleAndSiteAndEntityId(String title, int siteId, int entityId);

    @Query("""
            select se
            from ArticleSearchEntityAssociation asa
            inner join SearchEntity se on se.id = asa.searchEntity.id
            where asa.article.id = ?1
            order by se.name asc
            """)
    List<SearchEntity> findByAllByArticleId(int articleId);
}
