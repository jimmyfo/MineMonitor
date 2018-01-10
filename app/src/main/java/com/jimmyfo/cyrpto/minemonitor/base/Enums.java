package com.jimmyfo.cyrpto.minemonitor.base;

public enum Enums {

    PreferencesFileName("MyPrefsFile"),
    BaseNotificationInterval("300000"),// Five minutes
    //BaseNotificationInterval("5000"),// Five seconds
    UserWebAddress("https://www.durinsmine.com:9119/stats_address?address="),
    SiteWebAddress("https://www.durinsmine.com:9119/stats");

    private final String value;

    private Enums(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
