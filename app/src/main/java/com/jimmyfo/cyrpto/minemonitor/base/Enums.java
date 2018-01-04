package com.jimmyfo.cyrpto.minemonitor.base;

public enum Enums {
    UserWebAddress("https://www.durinsmine.com:9119/stats_address?address="),
    SiteWebAddress("https://www.durinsmine.com:9119/live_stats");

    private final String value;

    private Enums(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
