package co.com.bancolombia.usecase.getrole;

import co.com.bancolombia.model.roles.Role;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private GetRoleUseCase getRoleUseCase;

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
        when(roleRepository.findById(anyString())).thenReturn(Mono.just(role));

        // Act
        Mono<Role> result = getRoleUseCase.execute("admin");

        // Assert
        StepVerifier.create(result)
                .expectNext(role)
                .verifyComplete();
    }

    @Test
    void executeRoleNotFound() {
        // Arrange
        when(roleRepository.findById(anyString())).thenReturn(Mono.empty());

        // Act
        Mono<Role> result = getRoleUseCase.execute("nonexistent");

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
    }
}
