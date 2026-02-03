package org.homedecoration.identity.worker.dto.request;

import lombok.Data;
import org.homedecoration.identity.worker.entity.Worker;

@Data
public class UpdateWorkerProfileRequest {
    private String realName;
    private String city;
    private Boolean isPlatformWorker;
    private Worker.WorkStatus workStatus;
    private Boolean enabled;
}
