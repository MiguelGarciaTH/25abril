package arquivo.repository;

import arquivo.model.IntegrationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationLogRepository extends JpaRepository<IntegrationLog, Long> {
}
