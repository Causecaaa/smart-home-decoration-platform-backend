package org.homedecoration.houseRoomImage.service;

import lombok.RequiredArgsConstructor;
import org.homedecoration.houseRoom.entity.HouseRoom;
import org.homedecoration.houseRoom.service.HouseRoomService;
import org.homedecoration.houseRoomImage.entity.FurnitureRoomImage;
import org.homedecoration.houseRoomImage.repository.FurnitureRoomImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FurnitureRoomImageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final FurnitureRoomImageRepository roomImageRepository;
    private final HouseRoomService roomService;

    @Transactional
    public FurnitureRoomImage createImage(Long roomId, Long designerId, MultipartFile file) throws IOException {
        HouseRoom room = roomService.getRoomById(roomId);
        checkDesigner(room, designerId);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("必须上传文件");
        }

        String originalName = file.getOriginalFilename();
        String filename = System.currentTimeMillis() + "_" + originalName;
        Path path = Paths.get(uploadDir, "room", filename);
        Files.createDirectories(path.getParent());
        file.transferTo(path.toFile());

        FurnitureRoomImage image = new FurnitureRoomImage();
        image.setRoom(room);
        image.setImageUrl("/uploads/room/" + filename);

        return roomImageRepository.save(image);
    }

    @Transactional
    public void deleteImage(Long roomImageId, Long designerId) {
        FurnitureRoomImage image = roomImageRepository.findById(roomImageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "房间图片不存在"));

        checkDesigner(image.getRoom(), designerId);

        try {
            Path path = Paths.get(uploadDir + image.getImageUrl().replace("/uploads", ""));
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("删除图片文件失败: " + e.getMessage(), e);
        }

        roomImageRepository.delete(image);
    }

    private void checkDesigner(HouseRoom room, Long designerId) {
        if (!room.getDesignerId().equals(designerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "designerId不匹配");
        }
    }

    public FurnitureRoomImage[] listImages(Long roomId) {
        return roomImageRepository.findByRoomId(roomId).toArray(new FurnitureRoomImage[0]);
    }
}
