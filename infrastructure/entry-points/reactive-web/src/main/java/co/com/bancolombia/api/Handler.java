package co.com.bancolombia.api;

import co.com.bancolombia.model.roles.Role;
import co.com.bancolombia.usecase.createrole.CreateRoleUseCase;
import co.com.bancolombia.usecase.deleterole.DeleteRoleUseCase;
import co.com.bancolombia.usecase.getrole.GetRoleUseCase;
import co.com.bancolombia.usecase.updaterole.UpdateRoleUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class Handler {
    private final CreateRoleUseCase createRoleUseCase;
    private final GetRoleUseCase getRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;

    public Mono<ServerResponse> createRole(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Role.class)
                .flatMap(createRoleUseCase::execute)
                .flatMap(role -> ServerResponse.created(URI.create("/api/roles/" + role.getRoleName()))
                        .bodyValue(role));
    }

    public Mono<ServerResponse> getRole(ServerRequest serverRequest) {
        String roleName = serverRequest.pathVariable("name");
        return getRoleUseCase.execute(roleName)
                .flatMap(role -> ServerResponse.ok().bodyValue(role))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> updateRole(ServerRequest serverRequest) {
        String roleName = serverRequest.pathVariable("name");
        return serverRequest.bodyToMono(Role.class)
                .map(role -> role.toBuilder().roleName(roleName).build())
                .flatMap(updateRoleUseCase::execute)
                .flatMap(role -> ServerResponse.ok().bodyValue(role))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteRole(ServerRequest serverRequest) {
        String roleName = serverRequest.pathVariable("name");
        return deleteRoleUseCase.execute(roleName)
                .then(ServerResponse.noContent().build());
    }
}
