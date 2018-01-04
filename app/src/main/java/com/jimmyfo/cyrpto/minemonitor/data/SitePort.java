package com.jimmyfo.cyrpto.minemonitor.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SitePort {
    @JsonProperty("port")
    private Integer Port;

    @JsonProperty("difficulty")
    private Integer Difficulty;

    @JsonProperty("desc")
    private String Description;

    public Integer getPort() {
        return Port;
    }

    public Integer getDifficulty() {
        return Difficulty;
    }

    public String getDescription() {
        return Description;
    }

    public void setPort(Integer port) {
        Port = port;
    }

    public void setDifficulty(Integer difficulty) {
        Difficulty = difficulty;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
