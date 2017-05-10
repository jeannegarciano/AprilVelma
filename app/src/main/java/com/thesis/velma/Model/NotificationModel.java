package com.thesis.velma.Model;

/**
 * Created by andrewlaurienrsocia on 03/05/2017.
 */

public class NotificationModel {

    private String EventName;
    private String EventLocation;
    private String EventTime;
    private String EventId;
    private String status;

    public NotificationModel(String eventname, String eventlocation, String time, String eventid,String status) {
        this.EventName = eventname;
        this.EventLocation = eventlocation;
        this.EventTime = time;
        this.EventId = eventid;
        this.status = status;
    }


    public String getEventName() {
        return this.EventName;
    }

    public String getEventLocation() {
        return this.EventLocation;
    }

    public String getEventTime() {
        return this.EventTime;
    }

    public String getEventId() {
        return this.EventId;
    }


    public String getStatus() {
        return this.status;
    }
}

