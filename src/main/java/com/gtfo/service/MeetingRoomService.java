package com.gtfo.service;

import com.gtfo.common.utils.RoomUtils;
import com.gtfo.dao.MeetingRoomDAO;
import com.gtfo.dao.dto.MeetingRoomRequestDTO;
import com.gtfo.service.domain.FloorResponse;
import com.gtfo.service.domain.MeetingResponse;
import com.gtfo.web.config.DateUtils;
import com.gtfo.web.config.Floor;
import com.gtfo.web.config.FloorConfig;
import com.gtfo.web.config.MeetingRoomConfig;
import microsoft.exchange.webservices.data.core.enumeration.misc.error.ServiceError;
import microsoft.exchange.webservices.data.core.response.AttendeeAvailability;
import microsoft.exchange.webservices.data.misc.availability.AttendeeInfo;
import microsoft.exchange.webservices.data.misc.availability.GetUserAvailabilityResults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class MeetingRoomService {

    @Autowired
    private FloorConfig floorConfig;

    @Autowired
    private MeetingRoomDAO meetingRoomDAO;

    @Autowired
    private DateUtils dateUtils;

    public List<MeetingResponse> getMeetingRoomInfo(String roomName) {

        List<AttendeeInfo> attendees = new ArrayList<>();
        attendees.add(new AttendeeInfo(this.getEmailFromRoomName(roomName)));

        LocalDateTime start = dateUtils.getCurrentTime();
        LocalDateTime end = dateUtils.getCurrentTime().plusDays(1);

        List<MeetingResponse> meetingResponses = new ArrayList<>();
        Optional<GetUserAvailabilityResults> results = this.meetingRoomDAO.getMeetingRoomInfo(new MeetingRoomRequestDTO(attendees, start, end));
        if (!results.isPresent()) {
            return meetingResponses;
        }
        for (AttendeeAvailability attendeeAvailability : results.get().getAttendeesAvailability()) {
            if (attendeeAvailability.getErrorCode() == ServiceError.NoError) {
                meetingResponses.addAll(attendeeAvailability.getCalendarEvents().stream().map(
                    calendarEvent -> new MeetingResponse(calendarEvent.getDetails().getSubject(),
                        dateUtils.convert(calendarEvent.getStartTime()).toString(),
                        dateUtils.convert(calendarEvent.getEndTime()).toString()))
                    .collect(Collectors.toList()));
            }
        }
        return meetingResponses;
    }

    public List<FloorResponse> getFloorMeetingRoomInfo(String floor) {

        List<AttendeeInfo> attendees = new ArrayList<>();
        LocalDateTime start = dateUtils.getCurrentTime();
        LocalDateTime end = dateUtils.getCurrentTime().plusDays(1);

        Optional<MeetingRoomConfig> meetingRoomConfigOptional = this.floorConfig.getFloors().stream()
                .filter(floorConfig -> Floor.valueOf(floor) == floorConfig.getFloor()).findAny();
        meetingRoomConfigOptional.get().getMeetingRooms().forEach(meetingRoom -> attendees.add(new AttendeeInfo(meetingRoom.getEmail())));

        List<FloorResponse> floorResponseList = new ArrayList<>();
        Optional<GetUserAvailabilityResults> results = this.meetingRoomDAO.getMeetingRoomInfo(new MeetingRoomRequestDTO(attendees, start, end));
        if (!results.isPresent()) {
            return floorResponseList;
        }
        for (AttendeeAvailability attendeeAvailability : results.get().getAttendeesAvailability()) {
            if (attendeeAvailability.getErrorCode() == ServiceError.NoError) {
                List<MeetingResponse> meetingResponses = new ArrayList<>();
                FloorResponse floorResponse = new FloorResponse();
                meetingResponses.addAll(attendeeAvailability.getCalendarEvents().stream().map(
                    event ->  {
                        Optional<MeetingRoomConfig.MeetingRoom> meetingRoomOptional = meetingRoomConfigOptional.get().getMeetingRooms().stream().filter(
                                room -> StringUtils.isNotBlank(event.getDetails().getLocation()) && event.getDetails().getLocation().contains(room.getName())).findAny();
                        if (meetingRoomOptional.isPresent()) {
                            floorResponse.setName(meetingRoomOptional.get().getName());
                        }
                        return new MeetingResponse(event.getDetails().getSubject(),
                                dateUtils.convert(event.getStartTime()).toString(),
                                dateUtils.convert(event.getEndTime()).toString());
                    }).collect(Collectors.toList()));
                floorResponse.setSchedule(meetingResponses);
                floorResponseList.add(floorResponse);
            }
        }
        return floorResponseList;
    }

    public List<String> getAvailableMeetingRooms(String floor, String startTime) {
        List<AttendeeInfo> attendees = new ArrayList<>();
        List<String> unavailableRooms = new ArrayList<>();
        List<String> allRooms = new ArrayList<>();

        Optional<MeetingRoomConfig> meetingRoomConfigOptional = this.floorConfig.getFloors().stream()
                .filter(floorConfig -> Floor.valueOf(floor) == floorConfig.getFloor()).findAny();
        meetingRoomConfigOptional.get().getMeetingRooms().forEach(meetingRoom -> attendees.add(new AttendeeInfo(meetingRoom.getEmail())));

        LocalDateTime start;
        if (StringUtils.isNotBlank(startTime)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime time = LocalTime.parse(startTime, formatter);
            LocalDate date = LocalDate.now();
            start = date.atTime(time.getHour(), time.getMinute());
        } else {
            start = dateUtils.getCurrentTime();
        }
        LocalDateTime end = dateUtils.getCurrentTime().plusDays(1);
        Optional<GetUserAvailabilityResults> results = this.meetingRoomDAO.getMeetingRoomInfo(new MeetingRoomRequestDTO(attendees, start, end));
        if (!results.isPresent()) {
            return new ArrayList<>();
        }
        allRooms.addAll(meetingRoomConfigOptional.get().getMeetingRooms().stream().map(MeetingRoomConfig.MeetingRoom::getName).collect(Collectors.toList()));
        for (AttendeeAvailability attendeeAvailability : results.get().getAttendeesAvailability()) {
            if (attendeeAvailability.getErrorCode() == ServiceError.NoError) {
                unavailableRooms.addAll(attendeeAvailability.getCalendarEvents().stream()
                        .filter(calendarEvent -> start.plusMinutes(1).isAfter(dateUtils.convert(calendarEvent.getStartTime())) &&
                                start.plusMinutes(5).isBefore(dateUtils.convert(calendarEvent.getEndTime())))
                        .map(calendarEvent -> {
                            Optional<String> location = RoomUtils.getMeetingLocation(calendarEvent.getDetails().getLocation(), allRooms);
                            return location.isPresent() ? location.get() : null;
                        }).collect(Collectors.toList()));

            }
        }
        allRooms.removeAll(unavailableRooms);
        return allRooms;
    }

    public void assistanceEmail() {
        this.meetingRoomDAO.sendAssistEmail();
    }

    public void bookMeetingRoom(String roomName, String name, String startTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = LocalTime.parse(startTime, formatter);
        LocalDate date = LocalDate.now();
        LocalDateTime start = date.atTime(time.getHour(), time.getMinute());
        this.meetingRoomDAO.bookMeetingRoom(this.getEmailFromRoomName(roomName), name, start);
    }

    private String getEmailFromRoomName(String roomName) {
        List<MeetingRoomConfig> meetingRoomConfigs = this.floorConfig.getFloors();
        for (MeetingRoomConfig meetingRoomConfig : meetingRoomConfigs) {
            Optional<MeetingRoomConfig.MeetingRoom> meetingRoomOptional  = meetingRoomConfig.getMeetingRooms().stream()
                    .filter(meetingRoom -> meetingRoom.getName().equalsIgnoreCase(roomName)).findFirst();
            if (meetingRoomOptional.isPresent()) {
                return meetingRoomOptional.get().getEmail();
            }
        }
        return null;
    }
}
