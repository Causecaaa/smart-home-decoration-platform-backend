package org.homedecoration.identity.worker.LeaveRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.homedecoration.identity.worker.LeaveRecord.LeaveRecord;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRecordRepository extends JpaRepository<LeaveRecord, Long> {

    // 查某个工人的请假记录
    List<LeaveRecord> findByWorkerId(Long workerId);

    // 查某个工人在某天是否请假
    List<LeaveRecord> findByWorkerIdAndLeaveDate(Long workerId, LocalDate leaveDate);

    // 查某个城市、某日期请假的工人
    List<LeaveRecord> findByLeaveDate(LocalDate leaveDate);
}