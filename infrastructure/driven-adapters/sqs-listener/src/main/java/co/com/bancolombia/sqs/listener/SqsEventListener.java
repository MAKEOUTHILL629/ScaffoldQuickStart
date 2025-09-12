package co.com.bancolombia.sqs.listener;

import co.com.bancolombia.model.roleeventlog.RoleEventLog;
import co.com.bancolombia.model.roleeventlog.gateways.RoleEventLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class SqsEventListener {

    private final SqsAsyncClient sqsAsyncClient;
    private final RoleEventLogRepository roleEventLogRepository;
    private final ObjectMapper objectMapper; // Using Jackson ObjectMapper for more control

    @Value("${adapter.sqs.queueUrl}")
    private String queueUrl;
    @Value("${adapter.sqs.waitTimeSeconds:20}")
    private Integer waitTimeSeconds;


    @PostConstruct
    public void init() {
        // Start listening to the queue as soon as the bean is initialized
        listenToMessages()
                .subscribe(null,
                        error -> log.error("Error listening to SQS messages", error),
                        () -> log.info("SQS listener has completed"));
    }

    public Flux<Void> listenToMessages() {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(waitTimeSeconds)
                .build();

        return Flux.defer(() -> Mono.fromFuture(sqsAsyncClient.receiveMessage(receiveRequest)))
                .repeat()
                .flatMap(response -> Flux.fromIterable(response.messages()))
                .flatMap(this::processMessage, 10) // Concurrency of 10
                .doOnError(err -> log.error("Error in SQS processing pipeline", err))
                .onErrorResume(e -> Mono.delay(Duration.ofSeconds(10)).then(Mono.empty())); // Wait before retrying
    }

    private Mono<Void> processMessage(Message message) {
        return Mono.just(message)
                .flatMap(this::parseMessage)
                .flatMap(roleEventLogRepository::save)
                .doOnSuccess(savedLog -> log.info("Successfully processed and saved event {}", savedLog.getEventId()))
                .flatMap(savedLog -> deleteMessageFromQueue(message.receiptHandle()))
                .doOnError(err -> log.error("Error processing message: {}", message.messageId(), err))
                .onErrorResume(e -> Mono.empty()); // Acknowledge error and continue with next message
    }

    private Mono<RoleEventLog> parseMessage(Message message) {
        try {
            JsonNode rootNode = objectMapper.readTree(message.body());
            String eventType = rootNode.path("eventType").asText("UNKNOWN");

            RoleEventLog logEntry = RoleEventLog.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(eventType)
                    .timestamp(Instant.now())
                    .eventBody(message.body())
                    .build();
            return Mono.just(logEntry);
        } catch (JsonProcessingException e) {
            return Mono.error(new IllegalArgumentException("Failed to parse message JSON", e));
        }
    }

    private Mono<Void> deleteMessageFromQueue(String receiptHandle) {
        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build();
        return Mono.fromFuture(sqsAsyncClient.deleteMessage(deleteRequest)).then();
    }
}
