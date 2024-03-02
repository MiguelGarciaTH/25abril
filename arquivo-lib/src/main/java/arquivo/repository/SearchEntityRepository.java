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
            order by se.name asc
            """)
    List<SearchEntity> findAllByType(String type);

    @Query("""
            select DISTINCT s.type from SearchEntity s
            order by s.type asc
            """)
    List<String> findAllTypes();

    SearchEntity findByName(String mário_soares);
}
