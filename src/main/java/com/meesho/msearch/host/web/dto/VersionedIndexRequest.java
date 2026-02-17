package com.meesho.msearch.host.web.dto;

import com.meesho.msearch.es.model.requests.EsWriteRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class VersionedIndexRequest {
    @NotBlank
    private String version;
    @NotBlank
    private String indexName;
    private String clusterId;
    private String queryTimeout;
    private List<String> routingKeys;
    @NotNull
    @Valid
    private EsWriteRequest indexRequest;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getQueryTimeout() {
        return queryTimeout;
    }

    public void setQueryTimeout(String queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public List<String> getRoutingKeys() {
        return routingKeys;
    }

    public void setRoutingKeys(List<String> routingKeys) {
        this.routingKeys = routingKeys;
    }

    public EsWriteRequest getIndexRequest() {
        return indexRequest;
    }

    public void setIndexRequest(EsWriteRequest indexRequest) {
        this.indexRequest = indexRequest;
    }
}
