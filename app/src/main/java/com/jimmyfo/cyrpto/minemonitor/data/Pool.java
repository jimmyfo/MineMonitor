package com.jimmyfo.cyrpto.minemonitor.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pool {
    @JsonProperty("stats")
    private PoolStats Stats;

    @JsonProperty("blocks")
    private String[] blocks;

    @JsonProperty("totalBlocks")
    private int TotalBlocks;

    @JsonProperty("payments")
    private String[] Payments;

    @JsonProperty("totalPayments")
    private int TotalPayments;

    @JsonProperty("totalMinersPaid")
    private int TotalMinersPaid;

    @JsonProperty("miners")
    private int Miners;

    @JsonProperty("hashrate")
    private long HashRate;

    @JsonProperty("roundHashes")
    private long RoundHashes;

    @JsonProperty("lastBlockFound")
    private long LastBlockFound;

    public PoolStats getStats() {
        return Stats;
    }

    public String[] getBlocks() {
        return blocks;
    }

    public int getTotalBlocks() {
        return TotalBlocks;
    }

    public String[] getPayments() {
        return Payments;
    }

    public int getTotalPayments() {
        return TotalPayments;
    }

    public int getTotalMinersPaid() {
        return TotalMinersPaid;
    }

    public int getMiners() {
        return Miners;
    }

    public long getHashRate() {
        return HashRate;
    }

    public long getRoundHashes() {
        return RoundHashes;
    }

    public long getLastBlockFound() {
        return LastBlockFound;
    }

    public void setStats(PoolStats stats) {
        Stats = stats;
    }

    public void setBlocks(String[] blocks) {
        this.blocks = blocks;
    }

    public void setTotalBlocks(int totalBlocks) {
        TotalBlocks = totalBlocks;
    }

    public void setPayments(String[] payments) {
        Payments = payments;
    }

    public void setTotalPayments(int totalPayments) {
        TotalPayments = totalPayments;
    }

    public void setTotalMinersPaid(int totalMinersPaid) {
        TotalMinersPaid = totalMinersPaid;
    }

    public void setMiners(int miners) {
        Miners = miners;
    }

    public void setHashRate(long hashRate) {
        HashRate = hashRate;
    }

    public void setRoundHashes(long roundHashes) {
        RoundHashes = roundHashes;
    }

    public void setLastBlockFound(long lastBlockFound) {
        LastBlockFound = lastBlockFound;
    }
}
