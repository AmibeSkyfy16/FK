package ch.skyfy.fk.logic.data;


import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

public class TimelineData {

    @Getter @Setter
    private int day, minutes, seconds;

    @Getter @Setter
    private int timeOfDay;

    public TimelineData(int day, int minutes, int seconds, int timeOfDay) {
        this.day = day;
        this.minutes = minutes;
        this.seconds = seconds;
        this.timeOfDay = timeOfDay;
    }
}
