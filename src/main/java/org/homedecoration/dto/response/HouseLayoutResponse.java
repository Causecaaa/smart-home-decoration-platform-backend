package org.homedecoration.dto.response;

import lombok.Data;
import org.homedecoration.entity.HouseLayout;

import java.time.Instant;

@Data
public class HouseLayoutResponse {

    private Long layoutId;
    private Long userId;
    private Long houseId;

    private String layoutIntent;
    private String redesignNotes;
    private String layoutStatus;

    private Integer layoutVersion;
    private Instant createdAt;
    private Instant updatedAt;

    public static HouseLayoutResponse toDTO(HouseLayout layout) {
        HouseLayoutResponse dto = new HouseLayoutResponse();
        dto.setLayoutId(layout.getId());
        dto.setUserId(layout.getHouse().getUser().getId());
        dto.setHouseId(layout.getHouse().getId());
        dto.setLayoutIntent(String.valueOf(layout.getLayoutIntent()));
        dto.setRedesignNotes(layout.getRedesignNotes());
        dto.setLayoutStatus(layout.getLayoutStatus().name());
        dto.setLayoutVersion(layout.getLayoutVersion());
        dto.setCreatedAt(layout.getCreatedAt());
        dto.setUpdatedAt(layout.getUpdatedAt());
        return dto;
    }
}
