package org.homedecoration.bill.service;

import org.homedecoration.bill.dto.request.CreateBillRequest;
import org.homedecoration.bill.dto.response.BillResponse;
import org.homedecoration.bill.entity.Bill;
import org.homedecoration.bill.repository.BillRepository;
import org.homedecoration.common.exception.BusinessException;
import org.homedecoration.furnitureScheme.repository.FurnitureSchemeRepository;
import org.homedecoration.layout.repository.HouseLayoutRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final HouseLayoutRepository houseLayoutRepository;
    private final FurnitureSchemeRepository furnitureSchemeRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public BillService(
            BillRepository billRepository,
            HouseLayoutRepository houseLayoutRepository,
            FurnitureSchemeRepository furnitureSchemeRepository,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.billRepository = billRepository;
        this.houseLayoutRepository = houseLayoutRepository;
        this.furnitureSchemeRepository = furnitureSchemeRepository;
        this.redisTemplate = redisTemplate;
    }

    public Bill getBill(Bill.BizType bizType, Long bizId) {
        return billRepository.findByBizTypeAndBizId(bizType, bizId)
                .orElseThrow(() -> new RuntimeException("未找到对应账单"));
    }
    // 查询单条账单
    public Bill getBill(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("账单不存在"));
    }

    @Transactional
    public Bill createBill(CreateBillRequest request, Long payerId) {

        // 1️⃣ 防止重复账单
        boolean exists = billRepository.existsByBizTypeAndBizId(
                request.getBizType(),
                request.getBizId()
        );
        if (exists) {
            throw new RuntimeException("该阶段账单已存在，不可重复创建");
        }

        // 2️⃣ 金额合法性校验（非常重要）
        if (request.getAmount() == null
                || request.getDepositAmount() == null
                || request.getDepositAmount().compareTo(BigDecimal.ZERO) <= 0
                || request.getDepositAmount().compareTo(request.getAmount()) > 0) {
            throw new RuntimeException("账单金额或定金金额不合法");
        }

        // 3️⃣ 构建账单
        Bill bill = new Bill();
        bill.setBizType(request.getBizType());
        bill.setBizId(request.getBizId());

        bill.setAmount(request.getAmount());
        bill.setDepositAmount(request.getDepositAmount());

        bill.setPayStatus(Bill.PayStatus.UNPAID);
        bill.setPayerId(payerId);
        bill.setPayeeId(determinePayee(
                request.getBizType(),
                request.getBizId()
        ));
        bill.setRemark(request.getRemark());

        Bill saved = billRepository.save(bill);
        return saved;
    }



    private Long determinePayee(Bill.BizType bizType, Long bizId) {
        // 示例逻辑：设计费收给对应设计师
        switch (bizType) {
            case LAYOUT:
                return houseLayoutRepository.findById(bizId)
                        .orElseThrow(() -> new RuntimeException("Layout不存在"))
                        .getDesignerId();
            case FURNITURE:
                return furnitureSchemeRepository.findById(bizId)
                        .orElseThrow(() -> new RuntimeException("FurnitureScheme不存在"))
                        .getDesignerId();
            default:
                throw new RuntimeException("未知业务类型");
        }
    }



    @Transactional
    public Bill payDeposit(Long payerId, Long billId) {

        // ===== 1️⃣ 分布式锁，防止重复支付 =====
        String lockKey = "bill:pay:lock:" + billId;
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS); // 10秒锁

        if (Boolean.FALSE.equals(locked)) {
            throw new RuntimeException("支付正在处理中，请稍候");
        }

        try {
            Bill bill = getAndCheckBill(billId, payerId);

            // ===== 4️⃣ 仅允许 UNPAID → DEPOSIT_PAID =====
            if (bill.getPayStatus() != Bill.PayStatus.UNPAID) {
                throw new RuntimeException("当前账单状态不允许支付定金");
            }

            // ===== 5️⃣ 校验定金金额 =====
            if (bill.getDepositAmount() == null
                    || bill.getDepositAmount().compareTo(BigDecimal.ZERO) <= 0
                    || bill.getDepositAmount().compareTo(bill.getAmount()) > 0) {
                throw new RuntimeException("账单定金金额异常");
            }

            // ===== 6️⃣ 更新状态 & 时间 =====
            bill.setPayStatus(Bill.PayStatus.DEPOSIT_PAID);
            bill.setDepositPaidAt(Instant.now());

            return billRepository.save(bill);

        } finally {
            // ===== 8️⃣ 释放锁 =====
            redisTemplate.delete(lockKey);
        }
    }


    /* ===== 支付尾款 ===== */
    @Transactional
    public Bill payFinal(Long payerId, Long billId) {
        // 1️⃣ 分布式锁
        String lockKey = "bill:pay:lock:" + billId;
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);

        if (Boolean.FALSE.equals(locked)) {
            throw new RuntimeException("支付正在处理中，请稍候");
        }

        try {
            // 2️⃣ 公共校验
            Bill bill = getAndCheckBill(billId, payerId);

            // 3️⃣ 状态检查
            if (bill.getPayStatus() != Bill.PayStatus.DEPOSIT_PAID) {
                throw new BusinessException("请先支付定金");
            }

            // 4️⃣ 更新状态
            bill.setPayStatus(Bill.PayStatus.PAID);
            bill.setPaidAt(Instant.now());
            Bill saved = billRepository.save(bill);

            // 5️⃣ 缓存支付状态（可选）
            redisTemplate.opsForValue().set(
                    "bill:pay:status:" + billId,
                    saved.getPayStatus().name(),
                    1, TimeUnit.HOURS
            );

            return saved;

        } finally {
            // 6️⃣ 释放锁
            redisTemplate.delete(lockKey);
        }
    }


    /* ===== 公共校验 ===== */
    private Bill getAndCheckBill(Long billId, Long payerId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new BusinessException("账单不存在"));

        if (!bill.getPayerId().equals(payerId)) {
            throw new BusinessException("无权操作该账单");
        }

        return bill;
    }


}
