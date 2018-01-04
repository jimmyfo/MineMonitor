package com.jimmyfo.cyrpto.minemonitor.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PoolCharts {
    @JsonProperty("hashrate")
    private long[][] HashRate;

    @JsonProperty("workers")
    private long[][] Workers;

    @JsonProperty("difficulty")
    private long[][] Difficulty;

    @JsonProperty("price")
    private BigDecimal[][] Price;

    @JsonProperty("profit")
    private BigDecimal[][] Profit;

    public long[][] getHashRate() {
        return HashRate;
    }

    public long[][] getWorkers() {
        return Workers;
    }

    public long[][] getDifficulty() {
        return Difficulty;
    }

    public BigDecimal[][] getPrice() {
        return Price;
    }

    public BigDecimal[][] getProfit() {
        return Profit;
    }

    public void setHashRate(long[][] hashRate) {
        HashRate = hashRate;
    }

    public void setWorkers(long[][] workers) {
        Workers = workers;
    }

    public void setDifficulty(long[][] difficulty) {
        Difficulty = difficulty;
    }

    public void setPrice(BigDecimal[][] price) {
        Price = price;
    }

    public void setProfit(BigDecimal[][] profit) {
        Profit = profit;
    }
}
