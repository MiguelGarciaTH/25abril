package arquivo.repository;

import arquivo.model.Changelog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChangelogRepository extends JpaRepository<Changelog, Integer> {
    Optional<Changelog> findBySearchEntityIdAndSiteId(int searchEntityId, int siteId);
}
