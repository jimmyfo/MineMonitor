package com.jimmyfo.cyrpto.minemonitor.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MinerResponse {
    @JsonProperty("stats")
    private MinerStats MinerStats;

    @JsonProperty("payments")
    private String[] Payments;

    @JsonProperty("charts")
    private MinerCharts Charts;

    @JsonProperty("error")
    private String Error;

    public com.jimmyfo.cyrpto.minemonitor.data.MinerStats getMinerStats() {
        return MinerStats;
    }

    public String[] getPayments() {
        return Payments;
    }

    public MinerCharts getCharts() {
        return Charts;
    }

    public String getError() {
        return Error;
    }

    public void setMinerStats(com.jimmyfo.cyrpto.minemonitor.data.MinerStats minerStats) {
        MinerStats = minerStats;
    }

    public void setPayments(String[] payments) {
        Payments = payments;
    }

    public void setCharts(MinerCharts charts) {
        Charts = charts;
    }

    public void setError(String error) {
        Error = error;
    }
}
