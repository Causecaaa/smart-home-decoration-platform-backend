package org.homedecoration.bill.dto.request;

import lombok.Data;
import org.homedecoration.bill.entity.Bill;

import java.math.BigDecimal;

@Data
public class CreateBillRequest {

    private Bill.BizType bizType; // 业务类型：LAYOUT / FURNITURE / CONSTRUCTION

    private Long bizId;           // 可选，关联具体业务对象ID

    private BigDecimal amount;    // 本阶段金额

    private String remark;        // 可选备注
}
