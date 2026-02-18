package com.meesho.msearch.host.config;

import com.meesho.msearch.es.EsVersion;
import com.meesho.msearch.es.config.EsConnectionProperties;
import com.meesho.msearch.es.config.EsRetryConfig;
import com.meesho.msearch.es.repository.EsRepository;
import com.meesho.msearch.es.repository.EsRepositoryFactory;
import com.meesho.msearch.es.v8.repository.EsV8Repository;
import com.meesho.msearch.es.v813.repository.EsV813Repository;
import com.meesho.msearch.es.v91.repository.EsV91Repository;
import com.meesho.msearch.host.service.SearchTestService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(HostEsProperties.class)
public class HostEsConfig {

    @Bean
    public EsRetryConfig esRetryConfig() {
        return EsRetryConfig.builder().build();
    }

    @Bean
    public ApplicationRunner esRegistrationInitializer(HostEsProperties properties, EsRepositoryFactory repositoryFactory,
            EsRetryConfig retryConfig, SearchTestService searchTestService) {
        return args -> {
            if (properties.getEnabledVersions() == null || properties.getEnabledVersions().isEmpty()) {
                throw new IllegalArgumentException("host.es.enabled-versions must contain at least one ES version");
            }

            for (String versionValue : properties.getEnabledVersions()) {
                EsVersion version = EsVersion.fromValue(versionValue);
                EsRepository repository = buildRepository(version, retryConfig);
                repositoryFactory.registerRepository(version, repository);

                EsConnectionProperties connectionProperties = buildConnectionProperties(properties, version);
                searchTestService.registerClient(version, repository.createClient(connectionProperties));
            }
        };
    }

    private static EsRepository buildRepository(EsVersion version, EsRetryConfig retryConfig) {
        return switch (version) {
            case V8_5 -> new EsV8Repository(retryConfig);
            case V8_13 -> new EsV813Repository(retryConfig);
            case V9_1 -> new EsV91Repository(retryConfig);
            default -> throw new IllegalArgumentException(
                    "Unsupported ES version in host.es.enabled-versions: " + version.getValue());
        };
    }

    private static EsConnectionProperties buildConnectionProperties(HostEsProperties properties, EsVersion version) {
        VersionEsConnection versionEsConnection = properties.getVersions().get(version.getValue());
        if (versionEsConnection == null) {
            throw new IllegalStateException(
                    "No ES connection config for version " + version.getValue()
                            + ". Add host.es.versions." + version.getValue() + ".hosts (and optional username, password, socket-timeout).");
        }
        EsConnectionProperties connectionProperties = new EsConnectionProperties();
        connectionProperties.setHosts(versionEsConnection.getHosts());
        connectionProperties.setUsername(versionEsConnection.getUsername());
        connectionProperties.setPassword(versionEsConnection.getPassword());
        connectionProperties.setSocketTimeout(versionEsConnection.getSocketTimeout());
        return connectionProperties;
    }
}
