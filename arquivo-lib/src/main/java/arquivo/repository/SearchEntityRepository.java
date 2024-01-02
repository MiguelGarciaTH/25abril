package arquivo.repository;

import arquivo.model.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchEntityRepository extends JpaRepository<SearchEntity, Integer> {
}
