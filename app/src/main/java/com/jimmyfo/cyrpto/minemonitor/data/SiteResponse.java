package com.jimmyfo.cyrpto.minemonitor.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteResponse {
    @JsonProperty("config")
    private SiteConfig Config;

    @JsonProperty("network")
    private NetworkStats Network;

    @JsonProperty("pool")
    private Pool Pool;

    @JsonProperty("charts")
    private PoolCharts Charts;

    public SiteConfig getConfig() {
        return Config;
    }

    public NetworkStats getNetwork() {
        return Network;
    }

    public com.jimmyfo.cyrpto.minemonitor.data.Pool getPool() {
        return Pool;
    }

    public PoolCharts getCharts() {
        return Charts;
    }

    public void setConfig(SiteConfig config) {
        Config = config;
    }

    public void setNetwork(NetworkStats network) {
        Network = network;
    }

    public void setPool(com.jimmyfo.cyrpto.minemonitor.data.Pool pool) {
        Pool = pool;
    }

    public void setCharts(PoolCharts charts) {
        Charts = charts;
    }
}
