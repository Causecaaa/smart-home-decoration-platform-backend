package org.homedecoration.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.homedecoration.entity.HouseLayout;

@Data
public class CreateLayoutRequest {
    private Long userId;

    @NotNull
    private Long houseId;

    @NotNull
    private HouseLayout.LayoutIntent layoutIntent;

    private String redesignNotes;
}
