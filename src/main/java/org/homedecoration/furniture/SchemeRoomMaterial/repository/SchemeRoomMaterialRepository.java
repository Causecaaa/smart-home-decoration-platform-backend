package org.homedecoration.furniture.SchemeRoomMaterial.repository;

import org.homedecoration.furniture.SchemeRoomMaterial.entity.SchemeRoomMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchemeRoomMaterialRepository extends JpaRepository<SchemeRoomMaterial, Long> {

    /** 根据方案ID查材料信息 */
    Optional<SchemeRoomMaterial> findBySchemeId(Long schemeId);

    /** 根据房间ID查材料信息（可选） */
    Optional<SchemeRoomMaterial> findByRoomId(Long roomId);

    /** 删除方案时顺带清材料（一般用不上，留着） */
    void deleteBySchemeId(Long schemeId);
}