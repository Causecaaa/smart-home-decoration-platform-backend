package org.homedecoration.houseRoom.dto.response;

import lombok.Data;
import org.homedecoration.furnitureScheme.entity.FurnitureScheme;
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
     * 是否存在家具设计方案（任何状态）
     */
    private Boolean hasFurnitureScheme;

    /**
     * 是否已确认家具设计方案
     */
    private Boolean hasConfirmedScheme;

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

        dto.setHasFurnitureScheme(room.getSchemes() != null && !room.getSchemes().isEmpty()); // 有任何方案

        if (room.getSchemes() != null && !room.getSchemes().isEmpty()) {
            boolean hasConfirmedScheme = room.getSchemes().stream()
                    .anyMatch(scheme ->
                            scheme.getSchemeStatus() == FurnitureScheme.SchemeStatus.CONFIRMED
                    );
            dto.setHasConfirmedScheme(hasConfirmedScheme); // 有已确认方案
        } else {
            dto.setHasConfirmedScheme(false);
        }


        return dto;
    }
}
