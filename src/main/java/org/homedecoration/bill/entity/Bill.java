package org.homedecoration.bill.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Entity
@Table(
        name = "bill",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"biz_type", "biz_id"}
                )
        }
)
public class Bill {

    /* ===== 业务类型 ===== */
    public enum BizType {
        LAYOUT,
        FURNITURE,
        CONSTRUCTION,
        WAGE, //关联一个STAGE的工资订单
    }

    /* ===== 支付状态 ===== */
    public enum PayStatus {
        UNPAID,        // 未支付
        DEPOSIT_PAID,  // 已付定金
        PAID           // 已全部付清
    }

    /* ===== 主键 ===== */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ===== 业务关联 ===== */
    @Enumerated(EnumType.STRING)
    @Column(name = "biz_type", nullable = false, length = 20)
    private BizType bizType;

    @Column(name = "biz_id", nullable = false)
    private Long bizId;

    /* ===== 金额设计（重点） ===== */

    /**
     * 总金额（例如 5000）
     */
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    /**
     * 定金金额（例如 1500）
     */
    @Column(name = "deposit_amount", nullable = false)
    private BigDecimal depositAmount;

    /* ===== 支付状态 ===== */
    @Enumerated(EnumType.STRING)
    @Column(name = "pay_status", nullable = false, length = 20)
    private PayStatus payStatus = PayStatus.UNPAID;

    /* ===== 支付时间 ===== */
    @Column(name = "deposit_paid_at")
    private Instant depositPaidAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    /* ===== 支付双方 ===== */
    @Column(name = "payer_id", nullable = false)
    private Long payerId;

    @Column(name = "payee_id", nullable = false)
    private Long payeeId;

    /* ===== 备注 ===== */
    @Column(name = "remark", length = 255)
    private String remark;

    /* ===== 创建时间 ===== */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
