package org.homedecoration.identity.worker.repository;

import org.homedecoration.identity.worker.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {

    // 按城市查询可用工人
    List<Worker> findByCityAndWorkStatusAndEnabled(String city, Worker.WorkStatus workStatus, Boolean enabled);

}
