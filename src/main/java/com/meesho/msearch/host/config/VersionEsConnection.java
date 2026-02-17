package com.meesho.msearch.host.config;

import java.util.ArrayList;
import java.util.List;

/**
 * Per-version ES connection config: hosts, username, password, socket-timeout only.
 */
public class VersionEsConnection {
    private List<String> hosts = new ArrayList<>(List.of("localhost:9200"));
    private String username;
    private String password;
    private Integer socketTimeout;

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts != null ? hosts : new ArrayList<>();
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

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }
}
