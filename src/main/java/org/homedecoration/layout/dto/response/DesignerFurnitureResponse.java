package org.homedecoration.layout.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.homedecoration.bill.entity.Bill;
import org.homedecoration.furniture.furnitureScheme.entity.FurnitureScheme;
import org.homedecoration.house.entity.House;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.layout.entity.HouseLayout;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class DesignerFurnitureResponse {
    private Long layoutId;

    private Long userId;
    private String username;
    private String userEmail;
    private String userPhone;

    private Long houseId;
    private String communityName;
    private String buildingNo;
    private String unitNo;
    private String roomNo;
    private BigDecimal area;
    private String layoutType;
    private Integer floorCount;
    private House.DecorationType decorationType;
    private String designNotes;


    private FurnitureScheme.SchemeStatus furnitureStatus;


    private Long billId;
    private BigDecimal billAmount;
    private BigDecimal depositAmount;
    private Bill.PayStatus payStatus;
    private boolean finalSubmitted;


    public static DesignerFurnitureResponse toDTO(HouseLayout layout,
                                              FurnitureScheme.SchemeStatus furnitureStatus,
                                              Bill bill, boolean finalSubmitted) {
        DesignerFurnitureResponse dto = new DesignerFurnitureResponse();
        dto.setLayoutId(layout.getId());

        User user = layout.getHouse().getUser();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setUserEmail(user.getEmail());
        dto.setUserPhone(user.getPhone());

        House house = layout.getHouse();
        dto.setHouseId(house.getId());
        dto.setCommunityName(house.getCommunityName());
        dto.setBuildingNo(house.getBuildingNo());
        dto.setUnitNo(house.getUnitNo());
        dto.setRoomNo(house.getRoomNo());
        dto.setArea(house.getArea());
        dto.setLayoutType(house.getLayoutType());
        dto.setFloorCount(house.getFloorCount());
        dto.setDecorationType(house.getDecorationType());
        dto.setDesignNotes(layout.getFurnitureDesignNotes());

        dto.setFurnitureStatus(furnitureStatus);

        dto.setFinalSubmitted(finalSubmitted);

        if(bill != null){
            dto.setBillId(bill.getId());
            dto.setBillAmount(bill.getAmount());
            dto.setDepositAmount(bill.getDepositAmount());
            dto.setPayStatus(bill.getPayStatus());
        }

        return dto;
    }
}
