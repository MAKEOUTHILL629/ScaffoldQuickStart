package co.com.bancolombia.api.config;

import co.com.bancolombia.api.Handler;
import co.com.bancolombia.api.RouterRest;
import co.com.bancolombia.usecase.createrole.CreateRoleUseCase;
import co.com.bancolombia.usecase.deleterole.DeleteRoleUseCase;
import co.com.bancolombia.usecase.getrole.GetRoleUseCase;
import co.com.bancolombia.usecase.updaterole.UpdateRoleUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

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
    void corsConfigurationShouldAllowOrigins() {
        when(getRoleUseCase.execute("some-role")).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/roles/some-role")
                .exchange()
                .expectStatus().isNotFound() // isNotFound because the mocked use case returns empty
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}