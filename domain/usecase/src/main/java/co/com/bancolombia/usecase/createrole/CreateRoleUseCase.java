package co.com.bancolombia.usecase.createrole;

import co.com.bancolombia.model.roles.Role;
import co.com.bancolombia.model.roles.gateways.RoleEventPublisher;
import co.com.bancolombia.model.roles.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class CreateRoleUseCase {

    private final RoleRepository roleRepository;
    private final RoleEventPublisher roleEventPublisher;

    public Mono<Role> execute(Role role) {
        return roleRepository.save(role)
                .flatMap(savedRole -> roleEventPublisher.publishRoleEvent("ROLE_CREATED", savedRole)
                        .thenReturn(savedRole));
    }
}
