package org.homedecoration.furnitureScheme.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.homedecoration.furnitureScheme.dto.request.CreateFurnitureSchemeRequest;
import org.homedecoration.furnitureScheme.entity.FurnitureScheme;
import org.homedecoration.furnitureScheme.repository.FurnitureSchemeRepository;
import org.homedecoration.houseRoom.entity.HouseRoom;
import org.homedecoration.houseRoom.repository.HouseRoomRepository;
import org.homedecoration.layout.entity.HouseLayout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FurnitureSchemeService {

    private final FurnitureSchemeRepository schemeRepository;
    private final HouseRoomRepository roomRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 创建家具设计方案
     */
    // 在 FurnitureSchemeService 中
    @Transactional
    public FurnitureScheme createScheme(Long designerId, CreateFurnitureSchemeRequest request, MultipartFile file) throws IOException {
        var room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // 获取该房间已有方案的最大版本号
        Integer maxVersion = schemeRepository.findMaxVersionByRoom(room);
        int nextVersion = (maxVersion == null ? 1 : maxVersion + 1);

        FurnitureScheme scheme = new FurnitureScheme();
        scheme.setRoom(room);
        scheme.setDesignerId(designerId);
        scheme.setSchemeVersion(nextVersion);
        scheme.setSchemeStatus(FurnitureScheme.SchemeStatus.SUBMITTED);

        // 处理上传的图片
        if (file != null && !file.isEmpty()) {
            String imageUrl = saveImageFile(file);
            scheme.setImageUrl(imageUrl);
        }

        return schemeRepository.save(scheme);
    }

    // 更新方案图片（覆盖原图）
    @Transactional
    public FurnitureScheme updateSchemeImage(Long schemeId, MultipartFile file, Long designerId) throws IOException {
        FurnitureScheme scheme = getById(schemeId);

        // 权限校验
        if (!scheme.getDesignerId().equals(designerId)) {
            throw new SecurityException("无权限修改此方案");
        }

        if (scheme.getSchemeStatus() != FurnitureScheme.SchemeStatus.SUBMITTED) {
            throw new IllegalStateException("只能修改SUBMITTED状态的方案图片");
        }

        // 删除旧图片文件
        if (scheme.getImageUrl() != null) {
            deleteImageFile(scheme.getImageUrl());
        }

        // 保存新图片
        String newImageUrl = saveImageFile(file);
        scheme.setImageUrl(newImageUrl);

        return schemeRepository.save(scheme);
    }

    private String saveImageFile(MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        String filename = System.currentTimeMillis() + "_" + originalName;
        Path path = Paths.get(uploadDir + "/furniture", filename);
        Files.createDirectories(path.getParent());
        file.transferTo(path.toFile());

        return "/uploads/furniture/" + filename;
    }

    private void deleteImageFile(String imageUrl) {
        try {
            Path path = Paths.get(uploadDir + imageUrl.replace("/uploads", ""));
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // 记录日志，但不抛出异常，因为这不是关键操作
            System.err.println("删除旧图片失败: " + e.getMessage());
        }
    }

    @Transactional
    public List<FurnitureScheme> listByRoom(Long roomId) {
        HouseRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("房间不存在"));
        return schemeRepository.findByRoomOrderBySchemeVersionAsc(room);  // 改为升序
    }


    @Transactional
    public FurnitureScheme getById(Long schemeId) {
        return schemeRepository.findById(schemeId)
                .orElseThrow(() -> new IllegalArgumentException("方案不存在"));
    }

    @Transactional
    public FurnitureScheme confirmScheme(Long userId, Long schemeId) {

        FurnitureScheme scheme = getById(schemeId);

        HouseRoom room = scheme.getRoom();
        HouseLayout layout = room.getLayout();

        // 1️⃣ 权限校验：只有房屋所属用户才能确认
        if (!layout.getHouse().getUser().getId().equals(userId)) {
            throw new SecurityException("无权限确认该方案");
        }

        // 2️⃣ 状态校验
        if (scheme.getSchemeStatus() == FurnitureScheme.SchemeStatus.CONFIRMED) {
            throw new IllegalStateException("该方案已确认");
        }

        // 3️⃣ 只允许 SUBMITTED 状态被确认
        if (scheme.getSchemeStatus() != FurnitureScheme.SchemeStatus.SUBMITTED) {
            throw new IllegalStateException("当前方案不可确认");
        }

        // 4️⃣ 归档该房间下其他方案
        schemeRepository.archiveOtherSchemes(
                room,
                schemeId,
                FurnitureScheme.SchemeStatus.ARCHIVED
        );

        // 5️⃣ 确认当前方案
        scheme.setSchemeStatus(FurnitureScheme.SchemeStatus.CONFIRMED);

        return schemeRepository.save(scheme);
    }


    @Transactional
    public void deleteScheme(Long schemeId) {
        FurnitureScheme scheme = getById(schemeId);
        schemeRepository.delete(scheme);
    }

}
