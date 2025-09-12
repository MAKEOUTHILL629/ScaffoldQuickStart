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
import reactor.core.publisher.Mono;

import java.util.List;

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
                .value(userResponse -> {
                    assert userResponse.getRoleName().equals("admin");
                });
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
}
