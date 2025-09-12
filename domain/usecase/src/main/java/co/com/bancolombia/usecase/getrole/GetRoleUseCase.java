package co.com.bancolombia.usecase.getrole;

import co.com.bancolombia.model.roles.Role;
import co.com.bancolombia.model.roles.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetRoleUseCase {

    private final RoleRepository roleRepository;

    public Mono<Role> execute(String roleName) {
        return roleRepository.findById(roleName);
    }
}
