package com.y2gcoder.auth.common.infra;

import com.y2gcoder.auth.common.application.Time;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RealTime implements Time {

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

}
