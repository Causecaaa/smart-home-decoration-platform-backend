package org.homedecoration.furniture.furnitureScheme.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.homedecoration.furniture.SchemeRoomMaterial.entity.SchemeRoomMaterial;
import org.homedecoration.furniture.SchemeRoomMaterial.repository.SchemeRoomMaterialRepository;
import org.homedecoration.furniture.furnitureScheme.dto.request.CreateFurnitureSchemeRequest;
import org.homedecoration.furniture.furnitureScheme.dto.response.FurnitureSchemeResponse;
import org.homedecoration.furniture.furnitureScheme.entity.FurnitureScheme;
import org.homedecoration.furniture.furnitureScheme.repository.FurnitureSchemeRepository;
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
    private final SchemeRoomMaterialRepository schemeRoomMaterialRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 创建家具设计方案
     */
    // 在 FurnitureSchemeService 中
    @Transactional
    public FurnitureSchemeResponse createScheme(
            Long designerId,
            CreateFurnitureSchemeRequest request,
            MultipartFile file
    ) throws IOException {

        HouseRoom room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("房间不存在"));

        Integer maxVersion = schemeRepository.findMaxVersionByRoom(room);
        int nextVersion = (maxVersion == null ? 1 : maxVersion + 1);

        FurnitureScheme scheme = new FurnitureScheme();
        scheme.setRoom(room);
        scheme.setDesignerId(designerId);
        scheme.setSchemeVersion(nextVersion);
        scheme.setSchemeStatus(FurnitureScheme.SchemeStatus.SUBMITTED);

        if (file != null && !file.isEmpty()) {
            scheme.setImageUrl(saveImageFile(file));
        }

        FurnitureScheme savedScheme = schemeRepository.save(scheme);

        SchemeRoomMaterial material = new SchemeRoomMaterial();
        material.setSchemeId(savedScheme.getId());
        material.setRoomId(request.getRoomId());

        material.setFloorMaterial(request.getFloorMaterial());
        material.setFloorArea(request.getFloorArea());
        material.setWallMaterial(request.getWallMaterial());
        material.setWallArea(request.getWallArea());
        material.setCeilingMaterial(request.getCeilingMaterial());
        material.setCeilingArea(request.getCeilingArea());
        material.setCabinetMaterial(request.getCabinetMaterial());
        material.setCabinetArea(request.getCabinetArea());

        schemeRoomMaterialRepository.save(material);

        return FurnitureSchemeResponse.toDTO(savedScheme, material);
    }


    @Transactional
    public FurnitureSchemeResponse updateSchemeImage(
            Long schemeId,
            MultipartFile file,
            Long designerId
    ) throws IOException {

        FurnitureScheme scheme = getById(schemeId);

        if (!scheme.getDesignerId().equals(designerId)) {
            throw new SecurityException("无权限修改此方案");
        }

        if (scheme.getSchemeStatus() != FurnitureScheme.SchemeStatus.SUBMITTED) {
            throw new IllegalStateException("只能修改 SUBMITTED 状态方案");
        }

        if (scheme.getImageUrl() != null) {
            deleteImageFile(scheme.getImageUrl());
        }

        scheme.setImageUrl(saveImageFile(file));
        schemeRepository.save(scheme);

        SchemeRoomMaterial material =
                schemeRoomMaterialRepository.findBySchemeId(schemeId).orElse(null);

        return FurnitureSchemeResponse.toDTO(scheme, material);
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
    public List<FurnitureSchemeResponse> listByRoom(Long roomId) {

        HouseRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("房间不存在"));

        return schemeRepository.findByRoomOrderBySchemeVersionAsc(room)
                .stream()
                .map(scheme -> {
                    SchemeRoomMaterial material =
                            schemeRoomMaterialRepository
                                    .findBySchemeId(scheme.getId())
                                    .orElse(null);
                    return FurnitureSchemeResponse.toDTO(scheme, material);
                })
                .toList();
    }

    @Transactional
    public FurnitureSchemeResponse getSchemeDetail(Long schemeId) {

        FurnitureScheme scheme = getById(schemeId);
        SchemeRoomMaterial material =
                schemeRoomMaterialRepository.findBySchemeId(schemeId).orElse(null);

        return FurnitureSchemeResponse.toDTO(scheme, material);
    }


    @Transactional
    public FurnitureScheme getById(Long schemeId) {
        return schemeRepository.findById(schemeId)
                .orElseThrow(() -> new IllegalArgumentException("方案不存在"));
    }

    @Transactional
    public FurnitureSchemeResponse confirmScheme(Long userId, Long schemeId) {

        FurnitureScheme scheme = getById(schemeId);
        HouseRoom room = scheme.getRoom();
        HouseLayout layout = room.getLayout();

        if (!layout.getHouse().getUser().getId().equals(userId)) {
            throw new SecurityException("无权限确认该方案");
        }

        if (scheme.getSchemeStatus() != FurnitureScheme.SchemeStatus.SUBMITTED) {
            throw new IllegalStateException("当前方案不可确认");
        }

        schemeRepository.archiveOtherSchemes(
                room,
                schemeId,
                FurnitureScheme.SchemeStatus.ARCHIVED
        );

        scheme.setSchemeStatus(FurnitureScheme.SchemeStatus.CONFIRMED);
        schemeRepository.save(scheme);

        SchemeRoomMaterial material =
                schemeRoomMaterialRepository.findBySchemeId(schemeId).orElse(null);

        return FurnitureSchemeResponse.toDTO(scheme, material);
    }



    @Transactional
    public void deleteScheme(Long schemeId) {
        FurnitureScheme scheme = getById(schemeId);
        schemeRepository.delete(scheme);
    }

    /**
     * 获取房间的确认方案
     */
    @Transactional
    public FurnitureScheme getConfirmedSchemeByRoomId(Long roomId) {
        return (FurnitureScheme) schemeRepository.findByRoomIdAndSchemeStatus(
                roomId,
                FurnitureScheme.SchemeStatus.CONFIRMED
        ).orElse(null);
    }

}
