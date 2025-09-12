package co.com.bancolombia.usecase.deleterole;

import co.com.bancolombia.model.roles.gateways.RoleEventPublisher;
import co.com.bancolombia.model.roles.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteRoleUseCase {

    private final RoleRepository roleRepository;
    private final RoleEventPublisher roleEventPublisher;

    public Mono<Void> execute(String roleName) {
        // Find the role first to have its data for the event
        return roleRepository.findById(roleName)
                .flatMap(roleToBeDeleted ->
                        roleRepository.deleteById(roleName)
                                .then(roleEventPublisher.publishRoleEvent("ROLE_DELETED", roleToBeDeleted))
                );
    }
}
