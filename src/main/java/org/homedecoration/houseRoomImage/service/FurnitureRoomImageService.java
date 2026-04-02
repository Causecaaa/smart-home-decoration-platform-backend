package org.homedecoration.houseRoomImage.service;

import lombok.RequiredArgsConstructor;
import org.homedecoration.houseRoom.entity.HouseRoom;
import org.homedecoration.houseRoom.service.HouseRoomService;
import org.homedecoration.houseRoomImage.dto.request.CreateFurnitureRoomImageRequest;
import org.homedecoration.houseRoomImage.entity.FurnitureRoomImage;
import org.homedecoration.houseRoomImage.repository.FurnitureRoomImageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FurnitureRoomImageService {

    private final FurnitureRoomImageRepository roomImageRepository;
    private final HouseRoomService roomService;

    @Transactional
    public FurnitureRoomImage createImage(Long roomId, Long designerId, CreateFurnitureRoomImageRequest request) {
        HouseRoom room = roomService.getRoomById(roomId);
        checkDesigner(room, designerId);

        FurnitureRoomImage image = new FurnitureRoomImage();
        image.setRoom(room);
        image.setImageUrl(request.getImageUrl());

        return roomImageRepository.save(image);
    }

    @Transactional
    public void deleteImage(Long roomImageId, Long designerId) {
        FurnitureRoomImage image = roomImageRepository.findById(roomImageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "房间图片不存在"));

        checkDesigner(image.getRoom(), designerId);

        roomImageRepository.delete(image);
    }

    private void checkDesigner(HouseRoom room, Long designerId) {
        if (!room.getDesignerId().equals(designerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "designerId不匹配");
        }
    }
}
