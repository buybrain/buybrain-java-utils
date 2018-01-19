package io.buybrain.util.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface Clock extends Sleeper {
    Instant now();

    default ZonedDateTime nowUtc() {
        return now().atZone(ZoneId.of("UTC"));
    }
}
