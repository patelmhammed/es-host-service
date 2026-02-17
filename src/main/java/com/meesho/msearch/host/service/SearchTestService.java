package com.meesho.msearch.host.service;

import com.meesho.msearch.es.EsVersion;
import com.meesho.msearch.es.client.EsClient;
import com.meesho.msearch.es.client.EsClientInfo;
import com.meesho.msearch.es.client.EsClientManager;
import com.meesho.msearch.es.config.EsConnectionProperties;
import com.meesho.msearch.es.config.EsRequestProperties;
import com.meesho.msearch.es.model.requests.EsSearchRequest;
import com.meesho.msearch.es.model.requests.EsWriteRequest;
import com.meesho.msearch.es.model.responses.EsSearchResponse;
import com.meesho.msearch.es.model.responses.EsWriteResponse;
import com.meesho.msearch.es.repository.EsRepositoryFactory;
import com.meesho.msearch.host.config.HostEsProperties;
import com.meesho.msearch.host.config.VersionEsConnection;
import com.meesho.msearch.host.web.dto.VersionedIndexRequest;
import com.meesho.msearch.host.web.dto.VersionedSearchRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SearchTestService {
    private final EsClientManager esClientManager;
    private final EsRepositoryFactory esRepositoryFactory;
    private final HostEsProperties hostEsProperties;
    private final Map<EsVersion, EsClient> clientByVersion = new ConcurrentHashMap<>();

    public SearchTestService(EsClientManager esClientManager,
            EsRepositoryFactory esRepositoryFactory,
            HostEsProperties hostEsProperties) {
        this.esClientManager = esClientManager;
        this.esRepositoryFactory = esRepositoryFactory;
        this.hostEsProperties = hostEsProperties;
    }

    public Mono<EsSearchResponse> search(VersionedSearchRequest request) {
        EsVersion version = parseVersion(request.getVersion());
        EsSearchRequest searchRequest = withSearchDefaults(request.getSearchRequest());
        EsClientInfo clientInfo = createClientInfo(version, request.getIndexName(), request.getClusterId(),
                request.getQueryTimeout(), request.getRoutingKeys());
        CompletableFuture<EsSearchResponse> future = esRepositoryFactory.getRepository(version)
                .getDocuments(searchRequest, clientInfo);
        return Mono.fromFuture(future);
    }

    public Mono<EsWriteResponse> index(VersionedIndexRequest request) {
        EsVersion version = parseVersion(request.getVersion());
        EsClientInfo clientInfo = createClientInfo(version, request.getIndexName(), request.getClusterId(),
                request.getQueryTimeout(), request.getRoutingKeys());
        EsWriteRequest indexRequest = request.getIndexRequest();
        CompletableFuture<EsWriteResponse> future = esRepositoryFactory.getRepository(version)
                .indexBulkDocuments(indexRequest, clientInfo);
        return Mono.fromFuture(future);
    }

    private EsClientInfo createClientInfo(EsVersion version, String indexName, String clusterId,
            String queryTimeout, List<String> routingKeys) {
        EsClient client = clientByVersion.computeIfAbsent(version,
                v -> esClientManager.createClient(buildConnectionProperties(v), v));
        return EsClientInfo.builder()
                .esVersion(version)
                .esClient(client)
                .requestProperties(buildRequestProperties(version, indexName, clusterId, queryTimeout, routingKeys))
                .build();
    }

    private EsConnectionProperties buildConnectionProperties(EsVersion version) {
        VersionEsConnection vc = hostEsProperties.getVersions().get(version.getValue());
        if (vc == null) {
            throw new IllegalStateException(
                    "No ES connection config for version " + version.getValue()
                            + ". Add host.es.versions." + version.getValue() + ".hosts (and optional username, password, socket-timeout).");
        }
        EsConnectionProperties properties = new EsConnectionProperties();
        properties.setHosts(vc.getHosts());
        properties.setUsername(vc.getUsername());
        properties.setPassword(vc.getPassword());
        properties.setSocketTimeout(vc.getSocketTimeout());
        return properties;
    }

    private EsRequestProperties buildRequestProperties(EsVersion version, String indexName, String clusterId,
            String queryTimeout, List<String> routingKeys) {
        VersionEsConnection vc = hostEsProperties.getVersions().get(version.getValue());
        String firstHost = (vc != null && vc.getHosts() != null && !vc.getHosts().isEmpty())
                ? vc.getHosts().get(0) : null;
        EsRequestProperties properties = new EsRequestProperties();
        properties.setHost(firstHost);
        properties.setClusterId(clusterId);
        properties.setQueryTimeout(queryTimeout);
        properties.setIndexName(indexName);
        properties.setRoutingKeys(routingKeys);
        return properties;
    }

    private EsSearchRequest withSearchDefaults(EsSearchRequest request) {
        EsSearchRequest safeRequest = request == null ? new EsSearchRequest() : request;
        safeRequest.setSearchFields(safeRequest.getSearchFields() == null ? List.of() : safeRequest.getSearchFields());
        return safeRequest;
    }

    private EsVersion parseVersion(String versionValue) {
        return EsVersion.fromValue(versionValue);
    }
}
