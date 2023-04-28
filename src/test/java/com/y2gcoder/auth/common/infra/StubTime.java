package com.y2gcoder.auth.common.infra;

import com.y2gcoder.auth.common.application.Time;

import java.time.LocalDateTime;

public class StubTime implements Time {
    private final LocalDateTime currentTime;

    public StubTime(LocalDateTime currentTime) {
        this.currentTime = currentTime;
    }

    @Override
    public LocalDateTime now() {
        return this.currentTime;
    }
}
