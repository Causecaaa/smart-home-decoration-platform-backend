package org.homedecoration.identity.designer.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDesignerRequest {

    @Size(max = 50)
    private String realName;

    @Min(0)
    @Max(50)
    private Integer experienceYears;

    @Size(max = 100)
    private String style;

    @Size(max = 500)
    private String bio;
}
