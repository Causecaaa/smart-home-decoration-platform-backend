package org.homedecoration.identity.designer.dto.response;

import lombok.Data;
import org.homedecoration.house.entity.House;
import org.homedecoration.layout.entity.HouseLayout;

import java.math.BigDecimal;

@Data
public class LayoutResponse {
    private Long layoutId;
    private String redesignNotes;

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

    private Long userId;
    private String userName;
    private String phone;
    private String email;
    private String avatarUrl;

    public static LayoutResponse toDTO(HouseLayout houseLayout) {
        LayoutResponse layoutResponse = new LayoutResponse();
        layoutResponse.setLayoutId(houseLayout.getId());
        layoutResponse.setRedesignNotes(houseLayout.getRedesignNotes());
        layoutResponse.setHouseId(houseLayout.getHouse().getId());
        layoutResponse.setCity(houseLayout.getHouse().getCity());
        layoutResponse.setCommunityName(houseLayout.getHouse().getCommunityName());
        layoutResponse.setBuildingNo(houseLayout.getHouse().getBuildingNo());
        layoutResponse.setUnitNo(houseLayout.getHouse().getUnitNo());
        layoutResponse.setRoomNo(houseLayout.getHouse().getRoomNo());
        layoutResponse.setArea(houseLayout.getHouse().getArea());
        layoutResponse.setLayoutType(houseLayout.getHouse().getLayoutType());
        layoutResponse.setFloorCount(houseLayout.getHouse().getFloorCount());
        layoutResponse.setDecorationType(houseLayout.getHouse().getDecorationType());
        layoutResponse.setUserId(houseLayout.getDesignerId());
        layoutResponse.setUserName(houseLayout.getHouse().getUser().getUsername());
        layoutResponse.setPhone(houseLayout.getHouse().getUser().getPhone());
        layoutResponse.setEmail(houseLayout.getHouse().getUser().getEmail());
        layoutResponse.setAvatarUrl(houseLayout.getHouse().getUser().getAvatarUrl());

        return layoutResponse;
    }

}
