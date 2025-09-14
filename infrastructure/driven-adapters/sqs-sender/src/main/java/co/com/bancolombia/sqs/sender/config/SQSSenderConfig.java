package co.com.bancolombia.sqs.sender.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.auth.credentials.ContainerCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.SystemPropertyCredentialsProvider;
import software.amazon.awssdk.auth.credentials.WebIdentityTokenFileCredentialsProvider;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClientBuilder;

import java.net.URI;
import java.util.Optional;

@Configuration
@ConditionalOnMissingBean(SqsAsyncClient.class)
public class SQSSenderConfig {

    @Bean
    public SqsAsyncClient configSqs(SQSSenderProperties properties, MetricPublisher publisher) {
        SqsAsyncClientBuilder builder = SqsAsyncClient.builder()
                .region(Region.of(properties.region()))
                .overrideConfiguration(o -> o.addMetricPublisher(publisher));

        resolveEndpoint(properties).ifPresent(builder::endpointOverride);

        // For local testing, use AnonymousCredentialsProvider if an endpoint is set.
        // Otherwise, use the default chain for AWS environments.
        if (properties.endpoint() != null && !properties.endpoint().isBlank()) {
            builder.credentialsProvider(AnonymousCredentialsProvider.create());
        } else {
            builder.credentialsProvider(getProviderChain());
        }

        return builder.build();
    }

    private AwsCredentialsProviderChain getProviderChain() {
        return AwsCredentialsProviderChain.builder()
                .addCredentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .addCredentialsProvider(SystemPropertyCredentialsProvider.create())
                .addCredentialsProvider(WebIdentityTokenFileCredentialsProvider.create())
                .addCredentialsProvider(ProfileCredentialsProvider.create())
                .addCredentialsProvider(ContainerCredentialsProvider.builder().build())
                .addCredentialsProvider(InstanceProfileCredentialsProvider.create())
                .build();
    }

    private Optional<URI> resolveEndpoint(SQSSenderProperties properties) {
        if (properties.endpoint() != null && !properties.endpoint().isBlank()) {
            return Optional.of(URI.create(properties.endpoint()));
        }
        return Optional.empty();
    }
}
