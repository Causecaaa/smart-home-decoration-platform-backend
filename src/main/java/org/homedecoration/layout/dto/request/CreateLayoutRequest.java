package org.homedecoration.layout.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.homedecoration.layout.entity.HouseLayout;

@Data
public class CreateLayoutRequest {
    private Long userId;

    @NotNull
    private Long houseId;

    private Long designerId;

    @NotNull
    private HouseLayout.LayoutIntent layoutIntent;

    private String redesignNotes;
}
