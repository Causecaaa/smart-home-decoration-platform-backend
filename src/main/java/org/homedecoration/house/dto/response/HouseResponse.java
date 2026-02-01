package org.homedecoration.house.dto.response;

import lombok.Data;
import org.homedecoration.house.entity.House;

import java.math.BigDecimal;
import java.time.Instant;

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
    private Instant createdAt;
    private Instant updatedAt;

    private Long confirmedLayoutId;

    private Boolean canStartQuotation;
    private Boolean canStartConstruction;



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
        response.setConfirmedLayoutId(house.getConfirmedLayoutId());
        response.setCreatedAt(house.getCreatedAt());
        response.setUpdatedAt(house.getUpdatedAt());

        response.setCanStartQuotation(house.getCanStartQuotation());
        response.setCanStartConstruction(house.getCanStartConstruction());

        return response;
    }
}
