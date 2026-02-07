package org.homedecoration.stage.assignment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class InviteWorkersRequest {
    @NotNull(message = "日薪不能为空")
    private BigDecimal dailyWage;

    @NotEmpty(message = "工人列表不能为空")
    private List<Long> workerIds;
}
