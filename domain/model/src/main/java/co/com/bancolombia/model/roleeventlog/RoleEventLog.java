package co.com.bancolombia.model.roleeventlog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoleEventLog {
    private String eventId;
    private String eventType;
    private Instant timestamp;
    private String eventBody;
}
