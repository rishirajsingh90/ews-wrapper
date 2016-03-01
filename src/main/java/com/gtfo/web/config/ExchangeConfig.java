package com.gtfo.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExchangeConfig {

    @Value("${exchange.username}")
    private String username;

    @Value("${exchange.password}")
    private String password;

    @Value("${exchange.url}")
    private String url;

    @Value("${exchange.assist.subject}")
    private String subject;

    @Value("${exchange.assist.content}")
    private String content;

    @Value("${exchange.timezone}")
    private String timezone;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getTimezone() {
        return timezone;
    }
}
