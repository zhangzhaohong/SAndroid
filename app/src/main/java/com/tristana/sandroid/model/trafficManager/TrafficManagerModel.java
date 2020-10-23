package com.tristana.sandroid.model.trafficManager;

import java.io.Serializable;

public class TrafficManagerModel implements Serializable {

    private String id;
    private String redDuration;
    private String yellowDuration;
    private String greenDuration;

    public TrafficManagerModel(
            String id,
            String redDuration,
            String yellowDuration,
            String greenDuration
    ) {
        this.id = id;
        this.redDuration = redDuration;
        this.yellowDuration = yellowDuration;
        this.greenDuration = greenDuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRedDuration() {
        return redDuration;
    }

    public void setRedDuration(String redDuration) {
        this.redDuration = redDuration;
    }

    public String getYellowDuration() {
        return yellowDuration;
    }

    public void setYellowDuration(String yellowDuration) {
        this.yellowDuration = yellowDuration;
    }

    public String getGreenDuration() {
        return greenDuration;
    }

    public void setGreenDuration(String greenDuration) {
        this.greenDuration = greenDuration;
    }

}
