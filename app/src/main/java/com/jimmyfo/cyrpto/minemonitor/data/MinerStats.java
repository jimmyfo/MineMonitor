package com.jimmyfo.cyrpto.minemonitor.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MinerStats {
    @JsonProperty("hashes")
    private long Hashes;

    @JsonProperty("lastShare")
    private long LastShare;

    @JsonProperty("balance")
    private long Balance;

    @JsonProperty("paid")
    private long Paid;

    @JsonProperty("hashrate")
    private String HashRate;

    public long getHashes() {
        return Hashes;
    }

    public long getLastShare() {
        return LastShare;
    }

    public long getBalance() {
        return Balance;
    }

    public long getPaid() {
        return Paid;
    }

    public String getHashRate() {
        return HashRate;
    }

    public void setHashes(long hashes) {
        Hashes = hashes;
    }

    public void setLastShare(long lastShare) {
        LastShare = lastShare;
    }

    public void setBalance(long balance) {
        Balance = balance;
    }

    public void setPaid(long paid) {
        Paid = paid;
    }

    public void setHashRate(String hashRate) {
        HashRate = hashRate;
    }
}
