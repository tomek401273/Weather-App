package com.tgrajkowski.data;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class TestClock extends Clock {
    private final Instant instantTime;
    private final ZoneId zoneId = ZoneId.of("UTC");

    public TestClock(String instantTimeString) {
        this.instantTime = Instant.parse(instantTimeString);
    }

    @Override
    public ZoneId getZone() {
        return zoneId;
    }

    @Override
    public Clock withZone(ZoneId zoneId) {
        return Clock.system(zoneId);
    }

    @Override
    public Instant instant() {
        return instantTime;
    }
}
