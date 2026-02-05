package org.homedecoration.identity.worker.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.homedecoration.identity.worker.LeaveRecord.LeaveRecord;

import java.time.LocalDate;

@Setter
@Getter
public class LeaveRequest {
    @NotNull
    private LocalDate leaveDate;

    @NotNull
    LeaveRecord.LeaveType leaveType;

    @NotNull
    private String reason;
}
