package org.homedecoration.house.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.homedecoration.house.entity.House;

import java.math.BigDecimal;

@Data
public class CreateHouseRequest {
    @NotBlank(message = "城市不能为空")
    private String city;

    private String communityName;

    private String buildingNo;

    private String unitNo;

    private String roomNo;

    @NotNull(message = "建筑面积不能为空")
    private BigDecimal area;

    private String layoutType;

    @NotNull(message = "装修类型不能为空")
    private House.DecorationType decorationType;

    private Integer floorCount;
}
