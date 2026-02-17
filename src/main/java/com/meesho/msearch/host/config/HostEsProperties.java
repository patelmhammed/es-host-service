package com.meesho.msearch.host.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Host ES config: per-version connection (hosts, username, password, socket-timeout) and enabled versions only.
 */
@ConfigurationProperties(prefix = "host.es")
public class HostEsProperties {
    /** Per-version ES connection config. Key = version string (e.g. "8.5", "8.13", "9.1"). */
    private Map<String, VersionEsConnection> versions = new HashMap<>();
    private List<String> enabledVersions = new ArrayList<>(List.of("8.5", "8.13", "9.1"));

    public Map<String, VersionEsConnection> getVersions() {
        return versions;
    }

    public void setVersions(Map<String, VersionEsConnection> versions) {
        this.versions = versions != null ? versions : new HashMap<>();
    }

    public List<String> getEnabledVersions() {
        return enabledVersions;
    }

    public void setEnabledVersions(List<String> enabledVersions) {
        this.enabledVersions = enabledVersions != null ? enabledVersions : new ArrayList<>();
    }
}
