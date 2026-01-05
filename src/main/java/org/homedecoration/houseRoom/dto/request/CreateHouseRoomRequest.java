package org.homedecoration.houseRoom.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateHouseRoomRequest {

    private Long layoutId;

    private Integer floorNo;

    private String roomType;

    private String roomName;

    private BigDecimal area;

    private Boolean hasWindow;

    private Boolean hasBalcony;

    private String notes;
}
