package org.homedecoration.identity.worker.dto.response;

import lombok.Data;
import org.homedecoration.identity.worker.entity.Worker;

@Data
public class WorkerInfoResponse {
    private String realName;
    private String city;
    private Boolean isPlatformWorker;
    private Worker.WorkStatus status;


    public static WorkerInfoResponse toDTO(Worker worker) {
        WorkerInfoResponse dto = new WorkerInfoResponse();
        dto.setRealName(worker.getRealName());
        dto.setCity(worker.getCity());
        dto.setIsPlatformWorker(worker.getIsPlatformWorker());
        dto.setStatus(worker.getWorkStatus());

        return dto;
    }
}
