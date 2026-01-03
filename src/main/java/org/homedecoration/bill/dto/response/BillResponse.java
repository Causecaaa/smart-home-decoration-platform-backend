package org.homedecoration.bill.dto.response;

import lombok.Data;
import org.homedecoration.bill.entity.Bill;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class BillResponse {

    private Long id;
    private Bill.BizType bizType;
    private Long bizId;
    private BigDecimal amount;

    private Bill.PayStatus paymentStatus;
    private Long payerId;
    private Long payeeId;
    private String remark;

    private Instant createdAt;
    private Instant paidAt;

    public static BillResponse toDTO(Bill bill) {
        BillResponse response = new BillResponse();
        response.setId(bill.getId());
        response.setBizType(bill.getBizType());
        response.setBizId(bill.getBizId());
        response.setAmount(bill.getAmount());
        response.setPaymentStatus(bill.getPayStatus());
        response.setPayerId(bill.getPayerId());
        response.setPayeeId(bill.getPayeeId());
        response.setRemark(bill.getRemark());
        response.setCreatedAt(bill.getCreatedAt());
        response.setPaidAt(bill.getPaidAt());
        return response;
    }
}
