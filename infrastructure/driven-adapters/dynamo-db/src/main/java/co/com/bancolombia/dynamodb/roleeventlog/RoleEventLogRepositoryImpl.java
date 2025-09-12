package co.com.bancolombia.dynamodb.roleeventlog;

import co.com.bancolombia.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.model.roleeventlog.RoleEventLog;
import co.com.bancolombia.model.roleeventlog.gateways.RoleEventLogRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

@Repository
public class RoleEventLogRepositoryImpl extends TemplateAdapterOperations<RoleEventLog, String, RoleEventLogEntity> implements RoleEventLogRepository {

    public RoleEventLogRepositoryImpl(DynamoDbEnhancedAsyncClient connectionFactory,
                                      ObjectMapper mapper,
                                      @Value("${aws.dynamodb.event-log-table-name}") String tableName) {
        super(connectionFactory, mapper, d -> mapper.map(d, RoleEventLog.class), tableName);
    }

    // The save method is inherited from TemplateAdapterOperations and works for this implementation.
}
