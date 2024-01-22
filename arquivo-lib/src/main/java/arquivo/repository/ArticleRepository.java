package arquivo.repository;

import arquivo.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    Optional<Article> findByDigest(String arquivoDigest);

    @Query(nativeQuery = true, value = """
            select * 
            from article a
            where a.id in (
                select aes.article_id 
                from article_entity_association aes
                where aes.search_entity_id = ?1
            )
            """)
    List<Article> findByAllBySearchEntityId(int entityId);

    @Query(nativeQuery = true,
            value = """
                    select *
                    from article a
                    where a.url like %?1
                    """)
    Optional<Article> findByUrlLike(String queryKey);
}
