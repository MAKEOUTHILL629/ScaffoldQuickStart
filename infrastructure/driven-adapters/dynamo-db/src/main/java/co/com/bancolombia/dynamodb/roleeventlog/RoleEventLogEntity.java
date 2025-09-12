package co.com.bancolombia.dynamodb.roleeventlog;

import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.Instant;

@Data
@DynamoDbBean
@NoArgsConstructor
public class RoleEventLogEntity {

    private String eventId;
    private String eventType;
    private Instant timestamp;
    private String eventBody;

    @DynamoDbPartitionKey
    public String getEventId() {
        return this.eventId;
    }
}
