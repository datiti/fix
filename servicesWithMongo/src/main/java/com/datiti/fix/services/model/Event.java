package com.datiti.fix.services.model;

import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

public class Event {

    @Id
    private String id;

    private Timestamp ts;
    private String event;

    public Timestamp getTs() {
        return ts;
    }

    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
