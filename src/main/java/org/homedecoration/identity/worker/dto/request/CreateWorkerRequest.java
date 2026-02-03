package org.homedecoration.identity.worker.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.homedecoration.identity.worker.entity.Worker;

@Data
public class CreateWorkerRequest {
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "服务城市不能为空")
    private String city;

    private Boolean isPlatformWorker = true;

    private Worker.WorkStatus workStatus;
}
