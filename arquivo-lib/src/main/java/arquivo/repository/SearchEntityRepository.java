package arquivo.repository;

import arquivo.model.SearchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(nativeQuery = true, value = "SELECT se.* FROM search_entity se WHERE se.names_vector @@ to_tsquery('portuguese', :searchTerm)")
    Page<SearchEntity> findBySearchTerm(String searchTerm, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT se.* FROM search_entity se order by se.name asc")
    Page<SearchEntity> findAll(Pageable pageable);

}


