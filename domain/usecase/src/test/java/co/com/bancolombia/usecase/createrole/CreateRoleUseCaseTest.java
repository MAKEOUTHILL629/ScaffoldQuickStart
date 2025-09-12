package co.com.bancolombia.usecase.createrole;

import co.com.bancolombia.model.roles.Role;
import co.com.bancolombia.model.roles.gateways.RoleEventPublisher;
import co.com.bancolombia.model.roles.gateways.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RoleEventPublisher roleEventPublisher;

    @InjectMocks
    private CreateRoleUseCase createRoleUseCase;

    private Role role;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .roleName("admin")
                .permissions(List.of("CREATE", "READ"))
                .build();
    }

    @Test
    void executeSuccessfully() {
        // Arrange
        when(roleRepository.save(any(Role.class))).thenReturn(Mono.just(role));
        when(roleEventPublisher.publishRoleEvent(anyString(), any(Role.class))).thenReturn(Mono.empty());

        // Act
        Mono<Role> result = createRoleUseCase.execute(role);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(savedRole -> savedRole.getRoleName().equals("admin"))
                .verifyComplete();
    }

    @Test
    void executeWithRepositoryError() {
        // Arrange
        when(roleRepository.save(any(Role.class))).thenReturn(Mono.error(new RuntimeException("DB Error")));

        // Act
        Mono<Role> result = createRoleUseCase.execute(role);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && "DB Error".equals(throwable.getMessage()))
                .verify();
    }
}
