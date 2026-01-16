package org.homedecoration.layout.dto.response;

import lombok.Data;
import org.homedecoration.layout.entity.HouseLayout;

@Data
public class LayoutDesignerResponse {

    private Long layoutId;
    private Integer version;
    private HouseLayout.LayoutIntent layoutIntent;
    private String redesignNotes;
    private HouseLayout.LayoutStatus layoutStatus;

    public static LayoutDesignerResponse toDTO(HouseLayout layout) {
        LayoutDesignerResponse dto = new LayoutDesignerResponse();
        dto.setLayoutId(layout.getId());
        dto.setVersion(layout.getLayoutVersion());
        dto.setLayoutIntent(layout.getLayoutIntent());
        dto.setRedesignNotes(layout.getRedesignNotes());
        dto.setLayoutStatus(layout.getLayoutStatus());

        return dto;
    }
}
