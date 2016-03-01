package com.gtfo.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties
public class FloorConfig {

    private List<MeetingRoomConfig> floors;

    public List<MeetingRoomConfig> getFloors() {
        return floors;
    }

    public void setFloors(List<MeetingRoomConfig> floors) {
        this.floors = floors;
    }
}
