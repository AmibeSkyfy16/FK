package ch.skyfy.fk.logic.persistant;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the time of a fk game
 *
 * @see PersistantFKGameData
 * @see PersistantFKGame
 */
public class TimelineData implements Cloneable{

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

    @Override
    public TimelineData clone() {
        TimelineData timelineData  = null;
        try {
            timelineData = (TimelineData)super.clone();
        } catch(CloneNotSupportedException ignored) {}
        return timelineData;
    }

    @Override
    public String toString() {
        return "TimelineData{" +
                "day=" + day +
                ", minutes=" + minutes +
                ", seconds=" + seconds +
                ", timeOfDay=" + timeOfDay +
                '}';
    }
}
