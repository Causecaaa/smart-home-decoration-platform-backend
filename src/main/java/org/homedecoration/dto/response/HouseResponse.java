package org.homedecoration.dto.response;

import lombok.Data;
import org.homedecoration.entity.House;

import java.math.BigDecimal;

@Data
public class HouseResponse {
    private Long userId;
    private Long houseId;
    private String city;
    private String communityName;
    private String buildingNo;
    private String unitNo;
    private String roomNo;
    private BigDecimal area;
    private String layoutType;
    private Integer floorCount;
    private House.DecorationType decorationType;

    public static HouseResponse toDTO(House house) {
        HouseResponse response = new HouseResponse();
        response.setUserId(house.getUser().getId());
        response.setHouseId(house.getId());
        response.setCity(house.getCity());
        response.setCommunityName(house.getCommunityName());
        response.setBuildingNo(house.getBuildingNo());
        response.setUnitNo(house.getUnitNo());
        response.setRoomNo(house.getRoomNo());
        response.setArea(house.getArea());
        response.setLayoutType(house.getLayoutType());
        response.setFloorCount(house.getFloorCount());
        response.setDecorationType(house.getDecorationType());
        return response;
    }
}
