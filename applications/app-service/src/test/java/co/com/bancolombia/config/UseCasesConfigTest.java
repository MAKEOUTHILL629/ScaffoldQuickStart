package co.com.bancolombia.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            // This test just checks that beans ending with "UseCase" are being created.
            // By providing mock gateways, the real use cases can be instantiated.
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'UseCase' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class) // This imports the real configuration that scans for our use cases
    static class TestConfig {

        // Provide mock beans for the gateways that our real use cases depend on
        @Bean
        public co.com.bancolombia.model.roles.gateways.RoleRepository roleRepository() {
            return org.mockito.Mockito.mock(co.com.bancolombia.model.roles.gateways.RoleRepository.class);
        }

        @Bean
        public co.com.bancolombia.model.roles.gateways.RoleEventPublisher roleEventPublisher() {
            return org.mockito.Mockito.mock(co.com.bancolombia.model.roles.gateways.RoleEventPublisher.class);
        }
    }
}