package org.homedecoration.construction.stage.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StageScheduleRequest {
    private LocalDateTime expectedStartAt;
}
