package arquivo.repository;

import arquivo.model.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchEntityRepository extends JpaRepository<SearchEntity, Integer> {

    List<SearchEntity> findAllByOrderByIdDesc();

    @Query("""
            select s from SearchEntity s
            where s.type = ?1
            order by s.id desc
            """)
    List<SearchEntity> findAllByType(SearchEntity.Type type);

    @Query("""
            select DISTINCT s.type from SearchEntity s
            order by s.type asc
            """)
    List<String> findAllTypes();
}
