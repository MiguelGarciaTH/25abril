package arquivo.repository;

import arquivo.model.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchEntityRepository extends JpaRepository<SearchEntity, Long> {
}
