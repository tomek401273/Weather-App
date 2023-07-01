package com.tgrajkowski.configuration;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class ApplicationClock extends Clock {
    @Override
    public ZoneId getZone() {
        return ZoneId.systemDefault();
    }

    @Override
    public Clock withZone(ZoneId zoneId) {
        return Clock.system(zoneId);
    }

    @Override
    public Instant instant() {
        return Clock.systemDefaultZone().instant();
    }
}
