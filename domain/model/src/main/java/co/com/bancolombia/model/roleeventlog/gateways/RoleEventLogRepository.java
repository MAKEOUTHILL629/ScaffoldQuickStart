package co.com.bancolombia.model.roleeventlog.gateways;

import co.com.bancolombia.model.roleeventlog.RoleEventLog;
import reactor.core.publisher.Mono;

public interface RoleEventLogRepository {
    Mono<RoleEventLog> save(RoleEventLog eventLog);
}
