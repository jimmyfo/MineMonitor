package com.jimmyfo.cyrpto.minemonitor.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MinerCharts {
    @JsonProperty("payments")
    private String[][] Payments;

    @JsonProperty("hashrate")
    private String[][] HashRate;

    public String[][] getPayments() {
        return Payments;
    }

    public String[][] getHashRate() {
        return HashRate;
    }

    public void setPayments(String[][] payments) {
        Payments = payments;
    }

    public void setHashRate(String[][] hashRate) {
        HashRate = hashRate;
    }
}
