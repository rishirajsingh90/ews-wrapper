package com.gtfo.dao.dto;

import microsoft.exchange.webservices.data.misc.availability.AttendeeInfo;

import java.time.LocalDateTime;
import java.util.List;

public class MeetingRoomRequestDTO {

    private List<AttendeeInfo> attendeeInfoList;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public MeetingRoomRequestDTO(List<AttendeeInfo> attendeeInfoList, LocalDateTime startTime, LocalDateTime endTime) {
        this.attendeeInfoList = attendeeInfoList;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public List<AttendeeInfo> getAttendeeInfoList() {
        return attendeeInfoList;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
