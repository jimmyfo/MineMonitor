package com.jimmyfo.cyrpto.minemonitor.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PoolStats {
    @JsonProperty("lastBlockFound")
    private long LastBlockFound;

    public long getLastBlockFound() {
        return LastBlockFound;
    }

    public void setLastBlockFound(long lastBlockFound) {
        LastBlockFound = lastBlockFound;
    }
}
