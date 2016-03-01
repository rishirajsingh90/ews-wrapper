package com.gtfo.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class DateUtils {

    @Autowired
    private ExchangeConfig exchangeConfig;

    public LocalDateTime convert(Date date) {
        return date != null ? LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.of(exchangeConfig.getTimezone())) : null;
    }

    public Date convert(LocalDateTime dateTime) {
        return dateTime != null ? Date.from(dateTime.atZone(ZoneId.of(exchangeConfig.getTimezone())).toInstant()) : null;
    }

    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now(ZoneId.of(exchangeConfig.getTimezone()));
    }
}
