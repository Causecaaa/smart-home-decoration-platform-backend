package org.homedecoration.houseRoom.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.homedecoration.common.response.ApiResponse;
import org.homedecoration.common.utils.JwtUtil;
import org.homedecoration.houseRoom.dto.request.CreateHouseRoomRequest;
import org.homedecoration.houseRoom.dto.response.RoomResponse;
import org.homedecoration.houseRoom.entity.HouseRoom;
import org.homedecoration.houseRoom.service.HouseRoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class HouseRoomController {

    private final HouseRoomService roomService;
    private final JwtUtil jwtUtil;

    /**
     * 设计师创建房间
     */
    @PostMapping("/create")
    public ApiResponse<RoomResponse> createRoom(
            HttpServletRequest httpRequest,
            @RequestBody CreateHouseRoomRequest request
    ) {
        Long designerId = jwtUtil.getUserId(httpRequest);
        return ApiResponse.success(
                RoomResponse.toDTO(roomService.createRoom(designerId,request))
        );
    }

    @GetMapping("/{layoutId}/get-all")
    public ApiResponse<List<RoomResponse>> listRooms(
            @PathVariable Long layoutId
    ) {
        List<HouseRoom> rooms = roomService.listRoomsByLayout(layoutId);
        List<RoomResponse> roomResponses = rooms.stream()
                .map(RoomResponse::toDTO)
                .collect(Collectors.toList());
        return ApiResponse.success(roomResponses);
    }


    @GetMapping("/{roomId}/get")
    public ApiResponse<RoomResponse> getRoom(
            @PathVariable Long roomId
    ) {
        HouseRoom room = roomService.getRoomById(roomId);
        return ApiResponse.success(RoomResponse.toDTO(room));
    }

    @PutMapping("/{roomId}/update")
    public ApiResponse<RoomResponse> updateRoom(
            @PathVariable Long roomId,
            @RequestBody CreateHouseRoomRequest request
    ) {
        HouseRoom updatedRoom = roomService.updateRoom(roomId, request);
        return ApiResponse.success(RoomResponse.toDTO(updatedRoom));
    }

    @DeleteMapping("/{roomId}")
    public ApiResponse<Void> deleteRoom(
            @PathVariable Long roomId
    ) {
        roomService.deleteRoom(roomId);
        return ApiResponse.success(null);
    }
}
