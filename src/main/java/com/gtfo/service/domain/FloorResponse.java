package com.gtfo.service.domain;

import java.util.List;

public class FloorResponse {

    private String name;
    private List<MeetingResponse> schedule;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MeetingResponse> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<MeetingResponse> schedule) {
        this.schedule = schedule;
    }
}
