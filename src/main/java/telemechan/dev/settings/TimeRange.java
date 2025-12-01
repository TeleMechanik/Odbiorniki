package telemechan.dev.settings;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Setter
@Getter
public class TimeRange {
    private LocalTime from;

    private LocalTime to;

    public TimeRange(){}

    public TimeRange(LocalTime from, LocalTime to){
        this.from = from;
        this.to = to;
    }

    /**
     * @return true if the time is inside the time range
     */
    public boolean isWithinRange(LocalTime time) {
        return !time.isBefore(from) && !time.isAfter(to);
    }

    @Override
    public String toString() {
        return from + " - " + to;
    }
}
