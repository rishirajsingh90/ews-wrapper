package com.gtfo.web.vo;

import java.util.List;

public class FloorResponseVO {

    private String name;
    private List<MeetingResponseVO> schedule;

    public List<MeetingResponseVO> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<MeetingResponseVO> schedule) {
        this.schedule = schedule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
