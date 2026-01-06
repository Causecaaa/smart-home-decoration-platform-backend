package org.homedecoration.bill.service;

import org.homedecoration.bill.dto.request.CreateBillRequest;
import org.homedecoration.bill.dto.response.BillResponse;
import org.homedecoration.bill.entity.Bill;
import org.homedecoration.bill.repository.BillRepository;
import org.homedecoration.common.exception.BusinessException;
import org.homedecoration.furnitureScheme.repository.FurnitureSchemeRepository;
import org.homedecoration.layout.repository.HouseLayoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final HouseLayoutRepository houseLayoutRepository;
    private final FurnitureSchemeRepository furnitureSchemeRepository;

    public BillService(BillRepository billRepository, HouseLayoutRepository houseLayoutRepository, FurnitureSchemeRepository furnitureSchemeRepository) {
        this.billRepository = billRepository;
        this.houseLayoutRepository = houseLayoutRepository;
        this.furnitureSchemeRepository = furnitureSchemeRepository;
    }

    @Transactional
    public BillResponse createBill(CreateBillRequest request, Long payerId) {
        Bill bill = new Bill();
        bill.setBizType(request.getBizType());
        bill.setBizId(request.getBizId());
        bill.setAmount(request.getAmount());
        bill.setRemark(request.getRemark());
        bill.setPayerId(payerId);

        // 收款方根据业务类型决定
        bill.setPayeeId(determinePayee(request.getBizType(), request.getBizId()));

        Bill saved = billRepository.save(bill);
        return BillResponse.toDTO(saved);
    }

    private Long determinePayee(Bill.BizType bizType, Long bizId) {
        // 示例逻辑：设计费收给对应设计师
        switch (bizType) {
            case LAYOUT:
                return houseLayoutRepository.findById(bizId)
                        .orElseThrow(() -> new RuntimeException("Layout不存在"))
                        .getDesignerId();
            case FURNITURE:
//                return furnitureSchemeRepository.findById(bizId)
//                        .orElseThrow(() -> new RuntimeException("FurnitureScheme不存在"))
//                        .getDesignerId();
            default:
                throw new RuntimeException("未知业务类型");
        }
    }


    // 查询单条账单
    public Bill getBill(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("账单不存在"));
    }


    public Bill payDeposit(Long payerId, Long billId) {
        Bill bill = getAndCheckBill(billId, payerId);

        if (bill.getPayStatus() != Bill.PayStatus.UNPAID) {
            throw new BusinessException("当前账单不允许支付定金");
        }

        bill.setPayStatus(Bill.PayStatus.DEPOSIT_PAID);
        bill.setDepositPaidAt(Instant.now());

        return billRepository.save(bill);
    }

    /* ===== 支付尾款 ===== */
    public Bill payFinal(Long payerId, Long billId) {
        Bill bill = getAndCheckBill(billId, payerId);

        if (bill.getPayStatus() != Bill.PayStatus.DEPOSIT_PAID) {
            throw new BusinessException("请先支付定金");
        }

        bill.setPayStatus(Bill.PayStatus.PAID);
        bill.setPaidAt(Instant.now());

        return billRepository.save(bill);
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
