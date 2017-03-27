package io.buybrain.util.time;

import java.time.Instant;

public interface Clock extends Sleeper {
    Instant now();
}
