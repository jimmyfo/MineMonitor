package com.jimmyfo.cyrpto.minemonitor.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteConfig {
    @JsonProperty("ports")
    private SitePort[] SitePorts;

    @JsonProperty("hashrateWindow")
    private long HashrateWindow;

    @JsonProperty("fee")
    private float Fee;

    @JsonProperty("coin")
    private String Coin;

    @JsonProperty("coinUnits")
    private long CoinUnits;

    @JsonProperty("coinDifficultyTarget")
    private long CoinDifficultyTarget;

    @JsonProperty("symbol")
    private String Symbol;

    @JsonProperty("depth")
    private long Depth;

    // TODO
//    @JsonProperty("donation")
//    private String[] Donation;

    @JsonProperty("version")
    private String Version;

    @JsonProperty("minPaymentThreshold")
    private long MinimumPaymentThreshold;

    @JsonProperty("denominationUnit")
    private long DenominationUnit;

    public SitePort[] getSitePorts() {
        return SitePorts;
    }

    public long getHashrateWindow() {
        return HashrateWindow;
    }

    public float getFee() {
        return Fee;
    }

    public String getCoin() {
        return Coin;
    }

    public long getCoinUnits() {
        return CoinUnits;
    }

    public long getCoinDifficultyTarget() {
        return CoinDifficultyTarget;
    }

    public String getSymbol() {
        return Symbol;
    }

    public long getDepth() {
        return Depth;
    }

//    public String[] getDonation() {
//        return Donation;
//    }

    public String getVersion() {
        return Version;
    }

    public long getMinimumPaymentThreshold() {
        return MinimumPaymentThreshold;
    }

    public long getDenominationUnit() {
        return DenominationUnit;
    }

    public void setSitePorts(SitePort[] sitePorts) {
        SitePorts = sitePorts;
    }

    public void setHashrateWindow(long hashrateWindow) {
        HashrateWindow = hashrateWindow;
    }

    public void setFee(float fee) {
        Fee = fee;
    }

    public void setCoin(String coin) {
        Coin = coin;
    }

    public void setCoinUnits(long coinUnits) {
        CoinUnits = coinUnits;
    }

    public void setCoinDifficultyTarget(long coinDifficultyTarget) {
        CoinDifficultyTarget = coinDifficultyTarget;
    }

    public void setSymbol(String symbol) {
        Symbol = symbol;
    }

    public void setDepth(long depth) {
        Depth = depth;
    }

    //public void setDonation(String[] donation) {
    //    Donation = donation;
    //}

    public void setVersion(String version) {
        Version = version;
    }

    public void setMinimumPaymentThreshold(long minimumPaymentThreshold) {
        MinimumPaymentThreshold = minimumPaymentThreshold;
    }

    public void setDenominationUnit(long denominationUnit) {
        DenominationUnit = denominationUnit;
    }
}
