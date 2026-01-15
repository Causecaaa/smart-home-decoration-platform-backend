package org.homedecoration.layout.dto.response;

import lombok.Data;
import org.homedecoration.bill.dto.response.BillResponse;
import org.homedecoration.bill.entity.Bill;
import org.homedecoration.identity.user.entity.User;
import org.homedecoration.layout.entity.HouseLayout;

import java.math.BigDecimal;

@Data
public class CurrentLayoutResponse {

    // ===== Layout =====
    private Long layoutId;
    private Integer version;
    private HouseLayout.LayoutIntent layoutIntent;
    private String redesignNotes;
    private HouseLayout.LayoutStatus layoutStatus;

    // ===== Designer =====
    private Long designerId;
    private String designerUsername;
    private String designerEmail;

    // ===== Bill =====
    private Long billId;
    private BigDecimal billAmount;
    private BigDecimal depositAmount;
    private Bill.PayStatus payStatus;

    public static CurrentLayoutResponse toDTO(HouseLayout layout,
                                              Bill bill,
                                              User designer) {

        CurrentLayoutResponse resp = new CurrentLayoutResponse();

        // layout
        resp.setLayoutId(layout.getId());
        resp.setVersion(layout.getLayoutVersion());
        resp.setLayoutIntent(layout.getLayoutIntent());
        resp.setRedesignNotes(layout.getRedesignNotes());
        resp.setLayoutStatus(layout.getLayoutStatus());

        // designer
        resp.setDesignerId(designer.getId());
        resp.setDesignerUsername(designer.getUsername());
        resp.setDesignerEmail(designer.getEmail());

        // bill（⚠ 可能为空）
        if (bill != null) {
            resp.setBillId(bill.getId());
            resp.setBillAmount(bill.getAmount());
            resp.setDepositAmount(bill.getDepositAmount());
            resp.setPayStatus(bill.getPayStatus());
        }

        return resp;
    }

}