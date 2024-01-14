package arquivo.repository;

import arquivo.model.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchEntityRepository extends JpaRepository<SearchEntity, Integer> {

    List<SearchEntity> findAllByOrderByIdDesc();

    @Query(nativeQuery = true, value = """
            select * from search_entity se
            where se.type = ?1
            and se.id in (
                select distinct aes.search_entity_id
                from article_entity_association aes
            )
            order by se.id desc
            """)
    List<SearchEntity> findAllByType(String type);

    @Query("""
            select DISTINCT s.type from SearchEntity s
            order by s.type asc
            """)
    List<String> findAllTypes();
}
