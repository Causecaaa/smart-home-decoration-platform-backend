package org.homedecoration.identity.worker.dto.response;

import lombok.Data;
import org.homedecoration.bill.dto.response.BillResponse;

import java.util.List;

@Data
public class WorkerOrderResponse {
    List<WorkerSimpleResponse> workers;
    BillResponse bill;
}
