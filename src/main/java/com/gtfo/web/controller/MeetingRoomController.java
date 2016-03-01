package com.gtfo.web.controller;

import com.gtfo.common.utils.MapperUtils;
import com.gtfo.service.MeetingRoomService;
import com.gtfo.web.vo.MeetingResponseVO;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/meetingRoom")
public class MeetingRoomController {

    @Autowired
    private MeetingRoomService meetingRoomService;

    @Autowired
    private MapperUtils mapper;

    @RequestMapping(value = "/lookup/{roomName}", method = RequestMethod.GET)
    public List<MeetingResponseVO> meetingRoomLookup(@PathVariable String roomName) throws Exception {
        System.out.println("Received lookup request");
        return this.mapper.map(this.meetingRoomService.getMeetingRoomInfo(roomName), new TypeToken<List<MeetingResponseVO>>() {
        }.getType());
    }

    @RequestMapping(value = "/availability/{floor}", method = RequestMethod.GET)
    public List<String> availableMeetingRooms(@PathVariable("floor") String floor, @RequestParam(required = false) String startTime) throws Exception {
        System.out.println("Received availability request");
        return this.mapper.map(this.meetingRoomService.getAvailableMeetingRooms(floor, startTime), new TypeToken<List<String>>() {
        }.getType());
    }

    @RequestMapping(value = "/assistance", method = RequestMethod.POST)
    public void assistanceEmail() throws Exception {
        System.out.println("Received assistance request");
        this.meetingRoomService.assistanceEmail();
    }

    @RequestMapping(value = "/book/{roomName}/{name}", method = RequestMethod.POST)
    public void bookRoomfor(@PathVariable("roomName") String roomName, @PathVariable("name") String name,
                            @RequestParam(required = false) String startTime) throws Exception {
        System.out.println("Received booking request");
        this.meetingRoomService.bookMeetingRoom(roomName, name, startTime);
    }
}

