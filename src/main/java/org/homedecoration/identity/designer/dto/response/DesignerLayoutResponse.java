package org.homedecoration.identity.designer.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.homedecoration.identity.designer.entity.Designer;

@Getter
@Setter
public class DesignerLayoutResponse {

    private Long userId;
    private String name;
    private String email;

    public static DesignerLayoutResponse toDTO(Designer designer) {
        DesignerLayoutResponse dto = new DesignerLayoutResponse();
        dto.setUserId(designer.getUserId());
        dto.setName(designer.getRealName());
        dto.setEmail(designer.getUser().getEmail());
        return dto;
    }
}
