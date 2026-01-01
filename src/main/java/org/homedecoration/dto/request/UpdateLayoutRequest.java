package org.homedecoration.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.homedecoration.entity.HouseLayout;

@Data
public class UpdateLayoutRequest {

    @NotNull
    private HouseLayout.LayoutIntent layoutIntent;

    private String redesignNotes;
}

