package com.gtfo.dao;

import com.gtfo.dao.dto.MeetingRoomRequestDTO;
import com.gtfo.web.config.DateUtils;
import com.gtfo.web.config.ExchangeConfig;
import com.gtfo.web.config.FloorConfig;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.availability.AvailabilityData;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.misc.availability.GetUserAvailabilityResults;
import microsoft.exchange.webservices.data.misc.availability.TimeWindow;
import microsoft.exchange.webservices.data.property.complex.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@Validated
public class MeetingRoomDAO {

    @Autowired
    private ExchangeConfig exchangeConfig;

    @Autowired
    private DateUtils dateUtils;

    private ExchangeService service;

    @PostConstruct
    public void init() throws URISyntaxException {
        ExchangeCredentials credentials = new WebCredentials(exchangeConfig.getUsername(), exchangeConfig.getPassword());
        this.service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        this.service.setUrl(new URI(exchangeConfig.getUrl()));
        this.service.setCredentials(credentials);
    }

    public Optional<GetUserAvailabilityResults> getMeetingRoomInfo(MeetingRoomRequestDTO meetingRoomRequestDTO) {
        Optional<GetUserAvailabilityResults> getUserAvailabilityResults = Optional.empty();
        try {
            getUserAvailabilityResults = Optional.of(service.getUserAvailability(
                    meetingRoomRequestDTO.getAttendeeInfoList(),
                    new TimeWindow(dateUtils.convert(meetingRoomRequestDTO.getStartTime()), dateUtils.convert(meetingRoomRequestDTO.getEndTime())),
                    AvailabilityData.FreeBusyAndSuggestions));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getUserAvailabilityResults;
    }

    public void sendAssistEmail() {
        try {
            EmailMessage replymessage = new EmailMessage(service);
            EmailAddress fromEmailAddress = new EmailAddress(exchangeConfig.getUsername());
            replymessage.setFrom(fromEmailAddress);
            replymessage.getToRecipients().add(exchangeConfig.getUsername());
            replymessage.setSubject(exchangeConfig.getSubject());
            replymessage.setBody(new MessageBody(exchangeConfig.getContent()));
            replymessage.send();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bookMeetingRoom(String email, String name, LocalDateTime startTime) {
        try {
            LocalDateTime end = startTime.plusMinutes(30);
            Appointment appointment = new Appointment(service);
            appointment.setSubject("Office Insight Booking for " + name);
            appointment.setStart(dateUtils.convert(startTime));
            appointment.setEnd(dateUtils.convert(end));
            appointment.setLocation(email);
            appointment.setReminderMinutesBeforeStart(5);
            appointment.getRequiredAttendees().add(new Attendee(email));
            appointment.save(new FolderId(WellKnownFolderName.Calendar, new Mailbox(email)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
