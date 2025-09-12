package co.com.bancolombia.dynamodb.roles;

import co.com.bancolombia.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.model.roles.Role;
import co.com.bancolombia.model.roles.gateways.RoleRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;

@Repository
public class RoleRepositoryImpl extends TemplateAdapterOperations<Role, String, RoleEntity> implements RoleRepository {

    public RoleRepositoryImpl(DynamoDbEnhancedAsyncClient connectionFactory,
                              ObjectMapper mapper,
                              @Value("${aws.dynamodb.table-name}") String tableName) {
        super(connectionFactory, mapper, d -> mapper.map(d, Role.class), tableName);
    }

    @Override
    public Mono<Role> findById(String roleName) {
        return this.getById(roleName);
    }

    @Override
    public Mono<Void> deleteById(String roleName) {
        // The delete method in the template requires the model.
        // It will be mapped to the entity, and for deletion, only the key is required.
        return this.delete(Role.builder().roleName(roleName).build())
                .then();
    }
}
