package arquivo.repository;

import arquivo.model.SearchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchEntityRepository extends JpaRepository<SearchEntity, Integer> {

    @Query(nativeQuery = true, value = "SELECT se.* FROM search_entity se WHERE se.names_vector @@ to_tsquery('portuguese', :searchTerm) and se.type = :type")
    Page<SearchEntity> findBySearchTermAbdType(String searchTerm, String type, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT se.* FROM search_entity se order by se.name asc")
    Page<SearchEntity> findAll(Pageable pageable);

    @Query("""
            select new arquivo.repository.SearchEntityRepository$SearchEntityCounter(asea.searchEntity.id, asea.searchEntity.name, asea.searchEntity.imageUrl, count(asea.article.id))
            from ArticleSearchEntityAssociation asea
            where asea.entityScore > 0
            group by (asea.searchEntity.id, asea.searchEntity.name, asea.searchEntity.imageUrl)
            order by asea.searchEntity.name asc
            """)
    List<SearchEntityCounter> getSearchEntityCounts();

    @Query(nativeQuery = true, value = "SELECT se.* FROM search_entity se where se.type = ?1 order by se.name asc")
    Page<SearchEntity> findAllByType(String type, Pageable pageable);

    @Query(nativeQuery = true, value = """
            select distinct(se.type)
            from search_entity se
            """)
    List<String> getSearchEntityTypes();

    record SearchEntityCounter(int id, String name, String image, Long count) {

    }
}


