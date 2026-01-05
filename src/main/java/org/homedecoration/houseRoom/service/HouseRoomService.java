package org.homedecoration.houseRoom.service;

import lombok.RequiredArgsConstructor;
import org.homedecoration.houseRoom.dto.request.CreateHouseRoomRequest;
import org.homedecoration.houseRoom.entity.HouseRoom;
import org.homedecoration.houseRoom.repository.HouseRoomRepository;
import org.homedecoration.layout.entity.HouseLayout;
import org.homedecoration.layout.repository.HouseLayoutRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseRoomService {

    private final HouseRoomRepository roomRepository;
    private final HouseLayoutRepository layoutRepository;

    /**
     * 设计师创建房间（Layout 必须已确认）
     */
    @Transactional
    public HouseRoom createRoom(Long designerId, CreateHouseRoomRequest req) {

        HouseLayout layout = layoutRepository.findById(req.getLayoutId())
                .orElseThrow(() -> new IllegalArgumentException("Layout not found"));

        if (layout.getLayoutStatus() != HouseLayout.LayoutStatus.CONFIRMED) {
            throw new IllegalStateException("Layout is not confirmed, cannot create rooms");
        }

        HouseRoom room = new HouseRoom();
        room.setLayout(layout);
        room.setDesignerId(designerId);
        room.setFloorNo(req.getFloorNo());
        room.setRoomType(req.getRoomType());
        room.setRoomName(req.getRoomName());
        room.setArea(req.getArea());
        room.setHasWindow(req.getHasWindow());
        room.setHasBalcony(req.getHasBalcony());
        room.setNotes(req.getNotes());

        return roomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public List<HouseRoom> listRoomsByLayout(Long layoutId) {
        return roomRepository.findByLayoutId(layoutId);
    }

    @Transactional(readOnly = true)
    public HouseRoom getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("房间不存在"));
    }

    @Transactional
    public HouseRoom updateRoom(Long roomId, CreateHouseRoomRequest request) {
        HouseRoom room = getRoomById(roomId);

        // 可以更新的字段
        room.setFloorNo(request.getFloorNo());
        room.setRoomType(request.getRoomType());
        room.setRoomName(request.getRoomName());
        room.setArea(request.getArea());
        room.setHasWindow(request.getHasWindow());
        room.setHasBalcony(request.getHasBalcony());
        room.setNotes(request.getNotes());

        return roomRepository.save(room);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        HouseRoom room = getRoomById(roomId);
        roomRepository.delete(room);
    }

}
