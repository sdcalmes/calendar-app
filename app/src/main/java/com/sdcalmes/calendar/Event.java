package com.sdcalmes.calendar;

import java.util.Date;

/**
 * Created by sdcal on 3/7/2016.
 */
public class Event {

    String title;
    String description;
    Date date;
    int hour;
    int minute;

    public Event(String title, String description, Date date, int hour, int minute){
        this.description = description;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }


}
