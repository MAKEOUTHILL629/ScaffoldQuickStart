package co.com.bancolombia.model.roles.gateways;

import co.com.bancolombia.model.roles.Role;
import reactor.core.publisher.Mono;

public interface RoleEventPublisher {
    Mono<Void> publishRoleEvent(String eventType, Role role);
}
