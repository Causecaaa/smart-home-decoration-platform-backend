package org.homedecoration.layout.dto.response;

import lombok.Data;
import org.homedecoration.bill.entity.Bill;
import org.homedecoration.identity.designer.entity.Designer;
import org.homedecoration.layout.entity.HouseLayout;

import java.math.BigDecimal;

@Data
public class DraftLayoutResponse {

    // ===== Layout =====
    private Long layoutId;
    private Integer version;
    private HouseLayout.LayoutIntent layoutIntent;
    private String redesignNotes;
    private HouseLayout.LayoutStatus layoutStatus;

    private Long confirmedLayoutId;

    // ===== Designer =====
    private Long designerId;
    private String designerUsername;
    private String designerEmail;
    private String avatarUrl;

    // ===== Bill =====
    private Long billId;
    private BigDecimal billAmount;
    private BigDecimal depositAmount;
    private Bill.PayStatus payStatus;

    public static DraftLayoutResponse toDTO(HouseLayout layout,
                                            Long confirmedLayoutId,
                                            Bill bill,
                                            Designer designer) {

        DraftLayoutResponse resp = new DraftLayoutResponse();

        // layout
        resp.setLayoutId(layout.getId());
        resp.setVersion(layout.getLayoutVersion());
        resp.setLayoutIntent(layout.getLayoutIntent());
        resp.setRedesignNotes(layout.getRedesignNotes());
        resp.setLayoutStatus(layout.getLayoutStatus());

        resp.setConfirmedLayoutId(confirmedLayoutId);

        // designer
        resp.setDesignerId(designer.getUser().getId());
        resp.setDesignerUsername(designer.getRealName());
        resp.setDesignerEmail(designer.getUser().getEmail());
        resp.setAvatarUrl(designer.getUser().getAvatarUrl());

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