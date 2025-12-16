package telemechan.dev.settings;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Setter
@Getter
public class TimeRange {
    private LocalDateTime from;

    private LocalDateTime to;

    public TimeRange(){}

    public TimeRange(LocalDateTime from, LocalDateTime to){
        this.from = from;
        this.to = to;
    }

    /**
     * @return true if the time is inside the time range
     */
    public boolean isWithinRange(LocalDateTime time) {
        return !time.isBefore(from) && !time.isAfter(to);
    }

    @Override
    public String toString() {
        return from + " - " + to;
    }
}
