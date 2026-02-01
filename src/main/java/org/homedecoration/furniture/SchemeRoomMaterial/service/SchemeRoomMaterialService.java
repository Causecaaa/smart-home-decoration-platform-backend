package org.homedecoration.furniture.SchemeRoomMaterial.service;

import org.homedecoration.furniture.SchemeRoomMaterial.entity.SchemeRoomMaterial;
import org.homedecoration.furniture.SchemeRoomMaterial.repository.SchemeRoomMaterialRepository;
import org.homedecoration.furniture.furnitureScheme.entity.FurnitureScheme;
import org.homedecoration.furniture.furnitureScheme.service.FurnitureSchemeService;
import org.springframework.stereotype.Service;

@Service
public class SchemeRoomMaterialService {
    private final SchemeRoomMaterialRepository repository;
    private final FurnitureSchemeService furnitureSchemeService;
    public SchemeRoomMaterialService(SchemeRoomMaterialRepository repository, FurnitureSchemeService furnitureSchemeService) {
        this.repository = repository;
        this.furnitureSchemeService = furnitureSchemeService;
    }


    // 在 SchemeRoomMaterialService 中注入 FurnitureSchemeService
    public SchemeRoomMaterial getByRoomId(Long roomId) {
        // 获取房间的确认方案
        FurnitureScheme confirmedScheme = furnitureSchemeService.getConfirmedSchemeByRoomId(roomId);
        if (confirmedScheme == null) {
            return null;
        }
        // 根据方案ID获取材料信息
        return repository.findBySchemeId(confirmedScheme.getId())
                .orElse(null);
    }


    // 根据方案ID获取材料方案
    public SchemeRoomMaterial getBySchemeId(Long schemeId) {
        return repository.findBySchemeId(schemeId)
                .orElseThrow(() -> new RuntimeException("材料方案不存在"));
    }

    // 保存或更新材料方案
    public SchemeRoomMaterial saveMaterial(SchemeRoomMaterial material) {
        return repository.save(material);
    }
}
