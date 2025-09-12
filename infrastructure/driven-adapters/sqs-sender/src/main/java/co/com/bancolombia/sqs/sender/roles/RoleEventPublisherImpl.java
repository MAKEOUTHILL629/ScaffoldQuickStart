package co.com.bancolombia.sqs.sender.roles;

import co.com.bancolombia.model.roles.Role;
import co.com.bancolombia.model.roles.gateways.RoleEventPublisher;
import co.com.bancolombia.sqs.sender.SQSSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RoleEventPublisherImpl implements RoleEventPublisher {

    private final SQSSender sqsSender;
    private final ObjectMapper objectMapper; // Now using com.fasterxml.jackson.databind.ObjectMapper

    @Override
    public Mono<Void> publishRoleEvent(String eventType, Role role) {
        return Mono.fromCallable(() -> {
                    RoleEvent event = RoleEvent.builder()
                            .eventType(eventType)
                            .role(role)
                            .build();
                    try {
                        return objectMapper.writeValueAsString(event);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error serializing event", e);
                    }
                })
                .flatMap(sqsSender::send)
                .then();
    }

    @Data
    @Builder
    @AllArgsConstructor
    private static class RoleEvent {
        private String eventType;
        private Role role;
    }
}
