package co.com.bancolombia.usecase.deleterole;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteRoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RoleEventPublisher roleEventPublisher;

    @InjectMocks
    private DeleteRoleUseCase deleteRoleUseCase;

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
        when(roleRepository.findById("admin")).thenReturn(Mono.just(role));
        when(roleRepository.deleteById("admin")).thenReturn(Mono.empty());
        when(roleEventPublisher.publishRoleEvent(anyString(), any(Role.class))).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = deleteRoleUseCase.execute("admin");

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(roleRepository).findById("admin");
        verify(roleRepository).deleteById("admin");
        verify(roleEventPublisher).publishRoleEvent("ROLE_DELETED", role);
    }

    @Test
    void executeRoleNotFound() {
        // Arrange
        when(roleRepository.findById("nonexistent")).thenReturn(Mono.empty());

        // Act
        Mono<Void> result = deleteRoleUseCase.execute("nonexistent");

        // Assert
        StepVerifier.create(result)
                .verifyComplete(); // Should complete without doing anything
    }
}
