package io.buybrain.util.time;

import lombok.NonNull;
import lombok.Value;

import java.time.ZonedDateTime;
import java.time.temporal.TemporalUnit;

/**
 * Period of time represented by two ZonedDateTimes
 */
@Value
public class Period {
    @NonNull ZonedDateTime from;
    @NonNull ZonedDateTime to;

    public long length(@NonNull TemporalUnit unit) {
        return from.until(to, unit);
    }
}
