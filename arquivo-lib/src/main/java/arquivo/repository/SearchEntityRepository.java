package arquivo.repository;

import arquivo.model.SearchEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchEntityRepository extends JpaRepository<SearchEntity, Integer> {

    @Query(nativeQuery = true, value = "SELECT se.* FROM search_entity se WHERE se.names_vector @@ to_tsquery('portuguese', :searchTerm)")
    Page<SearchEntity> findBySearchTerm(String searchTerm, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT se.* FROM search_entity se order by se.name asc")
    Page<SearchEntity> findAll(Pageable pageable);

    @Query("""
            select new arquivo.repository.SearchEntityRepository$SearchEntityCounter(asea.searchEntity.id, asea.searchEntity.name, asea.searchEntity.imageUrl, count(asea.article.id))
            from ArticleSearchEntityAssociation asea
            group by (asea.searchEntity.id, asea.searchEntity.name, asea.searchEntity.imageUrl)
            order by asea.searchEntity.name asc
            """)
    List<SearchEntityCounter> getSearchEntityCounts();

    record SearchEntityCounter(int id, String name, String image, Long count) {

    }
}


