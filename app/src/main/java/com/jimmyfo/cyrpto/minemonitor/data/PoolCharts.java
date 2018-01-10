package com.jimmyfo.cyrpto.minemonitor.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PoolCharts {
    @JsonProperty("hashrate")
    private String[][] HashRate;

    @JsonProperty("workers")
    private String[][] Workers;

    @JsonProperty("difficulty")
    private String[][] Difficulty;

    @JsonProperty("price")
    private String[][] Price;

    @JsonProperty("profit")
    private String[][] Profit;

    public String[][] getHashRate() {
        return HashRate;
    }

    public String[][] getWorkers() {
        return Workers;
    }

    public String[][] getDifficulty() {
        return Difficulty;
    }

    public String[][] getPrice() {
        return Price;
    }

    public String[][] getProfit() {
        return Profit;
    }

    public void setHashRate(String[][] hashRate) {
        HashRate = hashRate;
    }

    public void setWorkers(String[][] workers) {
        Workers = workers;
    }

    public void setDifficulty(String[][] difficulty) {
        Difficulty = difficulty;
    }

    public void setPrice(String[][] price) {
        Price = price;
    }

    public void setProfit(String[][] profit) {
        Profit = profit;
    }
}
