package com.sdcalmes.calendar;

import java.util.Comparator;

/**
 * Created by sdcal on 3/7/2016.
 */
public class CustomComparator implements Comparator<Event> {
    @Override
    public int compare(Event e1, Event e2) {
        return e1.getDate().compareTo(e2.getDate());
    }
}