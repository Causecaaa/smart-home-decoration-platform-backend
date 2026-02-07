package org.homedecoration.stage.stage.dto.request;

import lombok.Data;

@Data
public class StageUpdateRequest {
    private Integer requiredCount;
    private Integer estimatedDay;
    String expectedStartAt;
}
