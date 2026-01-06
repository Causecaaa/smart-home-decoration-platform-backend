package org.homedecoration.furnitureScheme.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.homedecoration.furnitureScheme.dto.request.CreateFurnitureSchemeRequest;
import org.homedecoration.furnitureScheme.entity.FurnitureScheme;
import org.homedecoration.furnitureScheme.repository.FurnitureSchemeRepository;
import org.homedecoration.houseRoom.entity.HouseRoom;
import org.homedecoration.houseRoom.repository.HouseRoomRepository;
import org.homedecoration.layout.entity.HouseLayout;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FurnitureSchemeService {

    private final FurnitureSchemeRepository schemeRepository;
    private final HouseRoomRepository roomRepository;

    /**
     * 创建家具设计方案
     */
    @Transactional
    public FurnitureScheme createScheme(Long designerId, CreateFurnitureSchemeRequest request) {
        var room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // 获取该房间已有方案的最大版本号
        Integer maxVersion = schemeRepository.findMaxVersionByRoom(room);
        int nextVersion = (maxVersion == null ? 1 : maxVersion + 1);

        FurnitureScheme scheme = new FurnitureScheme();
        scheme.setRoom(room);
        scheme.setDesignerId(designerId);
        scheme.setSchemeVersion(nextVersion); // 自动递增
        scheme.setSchemeStatus(FurnitureScheme.SchemeStatus.SUBMITTED);

        return schemeRepository.save(scheme);
    }

    @Transactional
    public List<FurnitureScheme> listByRoom(Long roomId) {
        HouseRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("房间不存在"));
        return schemeRepository.findByRoomOrderBySchemeVersionDesc(room);
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
