package com.meesho.msearch.host.config;

import com.meesho.msearch.es.EsVersion;
import com.meesho.msearch.es.client.EsClientManager;
import com.meesho.msearch.es.config.EsRetryConfig;
import com.meesho.msearch.es.repository.EsRepositoryFactory;
import com.meesho.msearch.es.v8.config.EsV8ClientFactoryRegistrar;
import com.meesho.msearch.es.v813.config.EsV813ClientFactoryRegistrar;
import com.meesho.msearch.es.v91.config.EsV91ClientFactoryRegistrar;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(HostEsProperties.class)
public class HostEsConfig {

    @Bean
    public EsRetryConfig esRetryConfig(HostEsProperties properties) {
        return EsRetryConfig.builder()
                .maxAttempts(properties.getRetryMaxAttempts())
                .durationInSeconds(properties.getRetryDurationInSeconds())
                .jitterFactor(properties.getRetryJitterFactor())
                .build();
    }

    @Bean
    public ApplicationRunner esRegistrationInitializer(HostEsProperties properties, EsClientManager esClientManager,
            EsRepositoryFactory repositoryFactory, EsRetryConfig retryConfig) {
        return args -> {
            if (properties.getEnabledVersions() == null || properties.getEnabledVersions().isEmpty()) {
                throw new IllegalArgumentException("host.es.enabled-versions must contain at least one ES version");
            }

            for (String versionValue : properties.getEnabledVersions()) {
                EsVersion version = EsVersion.fromValue(versionValue);
                switch (version) {
                    case V8_5 -> EsV8ClientFactoryRegistrar.register(esClientManager, repositoryFactory, retryConfig);
                    case V8_13 -> EsV813ClientFactoryRegistrar.register(esClientManager, repositoryFactory, retryConfig);
                    case V9_1 -> EsV91ClientFactoryRegistrar.register(esClientManager, repositoryFactory, retryConfig);
                    default -> throw new IllegalArgumentException(
                            "Unsupported ES version in host.es.enabled-versions: " + versionValue);
                }
            }
        };
    }
}
