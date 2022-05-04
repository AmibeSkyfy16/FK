package ch.skyfy.fk.logic.data;


import lombok.Getter;
import lombok.Setter;

public class TimelineData {

    @Getter @Setter
    private int day, minutes, seconds;

    public TimelineData(int day, int minutes, int seconds) {
        this.day = day;
        this.minutes = minutes;
        this.seconds = seconds;
    }
}
