package co.com.bancolombia.dynamodb.roles;

import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;

@Data
@DynamoDbBean
@NoArgsConstructor
public class RoleEntity {

    private String roleName;
    private List<String> permissions;

    @DynamoDbPartitionKey
    public String getRoleName() {
        return this.roleName;
    }
}
