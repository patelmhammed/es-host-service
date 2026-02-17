package com.meesho.msearch.host.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "host.es")
public class HostEsProperties {
    private List<String> hosts = new ArrayList<>(List.of("localhost:9200"));
    private String scheme = "http";
    private int connectionRequestTimeout = 1_000;
    private int connectTimeout = 5_000;
    private int socketTimeout = 60_000;
    private boolean authEnabled;
    private String username;
    private String password;
    private int maxConnectionTotal = 30;
    private int maxConnectionPerRoute = 10;
    private String clusterId = "local";
    private String queryTimeout = "3s";
    private List<String> routingKeys = new ArrayList<>();
    private List<String> enabledVersions = new ArrayList<>(List.of("8.5", "8.13", "9.1"));
    private int defaultLimit = 10;
    private int retryMaxAttempts = 3;
    private long retryDurationInSeconds = 3;
    private double retryJitterFactor = 0.5d;

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public boolean isAuthEnabled() {
        return authEnabled;
    }

    public void setAuthEnabled(boolean authEnabled) {
        this.authEnabled = authEnabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxConnectionTotal() {
        return maxConnectionTotal;
    }

    public void setMaxConnectionTotal(int maxConnectionTotal) {
        this.maxConnectionTotal = maxConnectionTotal;
    }

    public int getMaxConnectionPerRoute() {
        return maxConnectionPerRoute;
    }

    public void setMaxConnectionPerRoute(int maxConnectionPerRoute) {
        this.maxConnectionPerRoute = maxConnectionPerRoute;
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

    public int getDefaultLimit() {
        return defaultLimit;
    }

    public void setDefaultLimit(int defaultLimit) {
        this.defaultLimit = defaultLimit;
    }

    public List<String> getEnabledVersions() {
        return enabledVersions;
    }

    public void setEnabledVersions(List<String> enabledVersions) {
        this.enabledVersions = enabledVersions;
    }

    public int getRetryMaxAttempts() {
        return retryMaxAttempts;
    }

    public void setRetryMaxAttempts(int retryMaxAttempts) {
        this.retryMaxAttempts = retryMaxAttempts;
    }

    public long getRetryDurationInSeconds() {
        return retryDurationInSeconds;
    }

    public void setRetryDurationInSeconds(long retryDurationInSeconds) {
        this.retryDurationInSeconds = retryDurationInSeconds;
    }

    public double getRetryJitterFactor() {
        return retryJitterFactor;
    }

    public void setRetryJitterFactor(double retryJitterFactor) {
        this.retryJitterFactor = retryJitterFactor;
    }
}
