package com.jimmyfo.cyrpto.minemonitor.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NetworkStats {
    @JsonProperty("difficulty")
    private long Difficulty;

    @JsonProperty("height")
    private long Height;

    @JsonProperty("timestamp")
    private long TimeStamp;

    @JsonProperty("reward")
    private long Reward;

    @JsonProperty("hash")
    private String Hash;

    public long getDifficulty() {
        return Difficulty;
    }

    public long getHeight() {
        return Height;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public long getReward() {
        return Reward;
    }

    public String getHash() {
        return Hash;
    }

    public void setDifficulty(long difficulty) {
        Difficulty = difficulty;
    }

    public void setHeight(long height) {
        Height = height;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }

    public void setReward(long reward) {
        Reward = reward;
    }

    public void setHash(String hash) {
        Hash = hash;
    }
}
