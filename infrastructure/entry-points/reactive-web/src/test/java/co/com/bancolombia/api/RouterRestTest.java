package co.com.bancolombia.api;

import co.com.bancolombia.model.roles.Role;
import co.com.bancolombia.usecase.createrole.CreateRoleUseCase;
import co.com.bancolombia.usecase.deleterole.DeleteRoleUseCase;
import co.com.bancolombia.usecase.getrole.GetRoleUseCase;
import co.com.bancolombia.usecase.updaterole.UpdateRoleUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateRoleUseCase createRoleUseCase;
    @MockBean
    private GetRoleUseCase getRoleUseCase;
    @MockBean
    private UpdateRoleUseCase updateRoleUseCase;
    @MockBean
    private DeleteRoleUseCase deleteRoleUseCase;

    @Test
    void testGetRole() {
        Role role = Role.builder().roleName("admin").permissions(List.of("read")).build();
        when(getRoleUseCase.execute("admin")).thenReturn(Mono.just(role));

        webTestClient.get()
                .uri("/api/roles/admin")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Role.class)
                .value(r -> assertThat(r.getRoleName()).isEqualTo("admin"));
    }

    @Test
    void testGetRoleNotFound() {
        when(getRoleUseCase.execute("notfound")).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/roles/notfound")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateRole() {
        Role roleToCreate = Role.builder().roleName("new-role").permissions(List.of("write")).build();
        when(createRoleUseCase.execute(any(Role.class))).thenReturn(Mono.just(roleToCreate));

        webTestClient.post()
                .uri("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(roleToCreate))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/api/roles/new-role")
                .expectBody(Role.class)
                .value(r -> assertThat(r.getRoleName()).isEqualTo("new-role"));
    }

    @Test
    void testUpdateRole() {
        Role roleToUpdate = Role.builder().roleName("admin").permissions(List.of("read", "write")).build();
        when(updateRoleUseCase.execute(any(Role.class))).thenReturn(Mono.just(roleToUpdate));

        webTestClient.put()
                .uri("/api/roles/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(roleToUpdate))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Role.class)
                .value(r -> assertThat(r.getPermissions()).contains("write"));
    }

    @Test
    void testDeleteRole() {
        when(deleteRoleUseCase.execute("admin")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/roles/admin")
                .exchange()
                .expectStatus().isNoContent();
    }
}
