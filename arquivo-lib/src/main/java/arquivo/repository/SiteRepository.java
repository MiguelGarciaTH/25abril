package arquivo.repository;

import arquivo.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SiteRepository extends JpaRepository<Site, Integer> {
    List<Site> findAllByOrderByIdDesc();
}
