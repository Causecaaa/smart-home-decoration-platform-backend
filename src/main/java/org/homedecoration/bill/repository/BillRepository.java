package org.homedecoration.bill.repository;

import org.homedecoration.bill.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    // 根据付款方查询账单
    List<Bill> findByPayerId(Long payerId);

    // 根据收款方查询账单
    List<Bill> findByPayeeId(Long payeeId);

    // 根据状态查询账单
    List<Bill> findByPayStatus(Bill.PayStatus payStatus);


    // 根据业务对象查询账单
    List<Bill> findByBizTypeAndBizId(Bill.BizType bizType, Long bizId);
}
