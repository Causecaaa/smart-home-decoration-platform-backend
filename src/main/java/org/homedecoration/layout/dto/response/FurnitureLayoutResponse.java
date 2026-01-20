package org.homedecoration.layout.dto.response;

import lombok.Data;
import org.homedecoration.bill.entity.Bill;
import org.homedecoration.furnitureScheme.entity.FurnitureScheme;
import org.homedecoration.identity.designer.entity.Designer;
import org.homedecoration.layout.entity.HouseLayout;

import java.math.BigDecimal;

@Data
public class FurnitureLayoutResponse {
    private Long userId;
    private Long houseId;
    private Long layoutId;

    private FurnitureScheme.SchemeStatus furnitureStatus;

    private Long furnitureDesignerId;
    private String designerUsername;
    private String designerEmail;

    private Long billId;
    private BigDecimal billAmount;
    private BigDecimal depositAmount;
    private Bill.PayStatus payStatus;

    public static FurnitureLayoutResponse toDTO(HouseLayout layout,
                                                FurnitureScheme.SchemeStatus furnitureStatus,
                                                Designer designer,
                                                Bill bill) {
        FurnitureLayoutResponse dto = new FurnitureLayoutResponse();
        dto.setLayoutId(layout.getId());
        dto.setUserId(layout.getHouse().getUser().getId());
        dto.setHouseId(layout.getHouse().getId());

        dto.setFurnitureStatus(furnitureStatus);

        if (designer != null){
            dto.setFurnitureDesignerId(designer.getUserId());
            dto.setDesignerUsername(designer.getRealName());
            dto.setDesignerEmail(designer.getUser().getEmail());
        }


        if(bill != null){
            dto.setBillId(bill.getId());
            dto.setBillAmount(bill.getAmount());
            dto.setDepositAmount(bill.getDepositAmount());
            dto.setPayStatus(bill.getPayStatus());
        }

        return dto;
    }
}
