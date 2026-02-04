package org.homedecoration.identity.worker.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class LeaveRequest {
    @NotNull
    private LocalDate leaveDate;

}
