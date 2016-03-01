package com.gtfo.web.vo;

public class MeetingResponseVO {

    private String email;
    private String startDate;
    private String endDate;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public MeetingResponseVO(String email, String startDate, String endDate) {
        this.email = email;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public MeetingResponseVO() {}
}
