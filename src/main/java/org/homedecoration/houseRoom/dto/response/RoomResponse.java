package org.homedecoration.houseRoom.dto.response;

import lombok.Data;
import org.homedecoration.houseRoom.entity.HouseRoom;

import java.math.BigDecimal;

@Data
public class RoomResponse {

    private Long roomId;

    private Long layoutId;

    private Long designerId;

    private Integer floorNo;

    private String roomType;

    private String roomName;

    private BigDecimal area;

    private Boolean hasWindow;

    private Boolean hasBalcony;

    private String notes;

    /**
     * 是否已经存在家具设计方案
     */
    private Boolean hasFurnitureScheme;

    public static RoomResponse toDTO(HouseRoom room) {
        RoomResponse dto = new RoomResponse();

        dto.setRoomId(room.getId());
        dto.setLayoutId(room.getLayout().getId());
        dto.setFloorNo(room.getFloorNo());
        dto.setDesignerId(room.getDesignerId());
        dto.setRoomType(room.getRoomType());
        dto.setRoomName(room.getRoomName());
        dto.setArea(room.getArea());
        dto.setHasWindow(room.getHasWindow());
        dto.setHasBalcony(room.getHasBalcony());
        dto.setNotes(room.getNotes());

        // 只关心“有没有家具设计”，不暴露 scheme 细节
        dto.setHasFurnitureScheme(
                room.getSchemes() != null && !room.getSchemes().isEmpty()
        );

        return dto;
    }
}
