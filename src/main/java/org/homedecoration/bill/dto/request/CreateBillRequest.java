package org.homedecoration.bill.dto.request;

import lombok.Data;
import org.homedecoration.bill.entity.Bill;

import java.math.BigDecimal;

@Data
public class CreateBillRequest {

    private Bill.BizType bizType;

    private Long bizId;

    /**
     * 总金额
     */
    private BigDecimal amount;

    /**
     * 定金金额
     */
    private BigDecimal depositAmount;

    private String remark;
}
