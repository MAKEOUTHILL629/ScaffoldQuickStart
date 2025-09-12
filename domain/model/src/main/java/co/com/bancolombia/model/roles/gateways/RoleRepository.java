package co.com.bancolombia.model.roles.gateways;

import co.com.bancolombia.model.roles.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Mono<Role> save(Role role);

    Mono<Role> findById(String roleName);

    Mono<Void> deleteById(String roleName);
}
