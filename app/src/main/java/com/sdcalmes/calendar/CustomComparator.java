package com.sdcalmes.calendar;

import java.util.Comparator;

/**
 * Created by sdcal on 3/7/2016.
 */
public class CustomComparator implements Comparator<Event> {
    @Override
    public int compare(Event e1, Event e2) {
        int compare = e1.getDate().compareTo(e2.getDate());
        if(compare != 0) return compare;

        compare = Integer.compare(e1.getHour(), e2.getHour());
        if(compare != 0) return compare;

        compare = Integer.compare(e1.getMinute(), e2.getMinute());
        return compare;
    }
}