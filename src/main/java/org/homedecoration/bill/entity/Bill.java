package org.homedecoration.bill.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(name = "bill")
public class Bill {

    public enum BizType {
        HOUSE,
        LAYOUT,
        FURNITURE,
        CONSTRUCTION
    }

    public enum PayStatus {
        UNPAID,        // 未支付
        DEPOSIT_PAID,  // 已付定金
        PAID           // 已付清（尾款完成）
    }
//

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "biz_type", nullable = false, length = 20)
    private BizType bizType;

    @Column(name = "biz_id")
    private Long bizId; // 可选，关联具体业务对象，例如 layout.id / furniture.id

    @Column(name = "amount", nullable = false)
    private BigDecimal amount; // 本期金额

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_status", nullable = false, length = 20)
    private PayStatus payStatus = PayStatus.UNPAID;

    @Column(name = "deposit_amount")
    private BigDecimal depositAmount;

    @Column(name = "deposit_paid_at")
    private Instant depositPaidAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "payer_id", nullable = false)
    private Long payerId; // 支付方用户ID

    @Column(name = "payee_id", nullable = false)
    private Long payeeId; // 收款方用户ID（平台 / 设计师 / 商家）

    @Column(name = "remark")
    private String remark; // 备注，例如“第1轮布局设计费”

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
