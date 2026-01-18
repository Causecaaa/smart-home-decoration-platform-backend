package org.homedecoration.furnitureScheme.repository;

import org.homedecoration.furnitureScheme.entity.FurnitureScheme;
import org.homedecoration.houseRoom.entity.HouseRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FurnitureSchemeRepository extends JpaRepository<FurnitureScheme, Long> {
    // 根据布局查设计方案
    boolean existsByRoom_Layout_Id(Long layoutId);

    @Query("SELECT MAX(s.schemeVersion) FROM FurnitureScheme s WHERE s.room = :room")
    Integer findMaxVersionByRoom(@Param("room") HouseRoom room);

    List<FurnitureScheme> findByRoomOrderBySchemeVersionDesc(HouseRoom room);

    @Modifying
    @Query("""
    update FurnitureScheme fs
    set fs.schemeStatus = :status
    where fs.room = :room
      and fs.id <> :schemeId
""")
    void archiveOtherSchemes(
            @Param("room") HouseRoom room,
            @Param("schemeId") Long schemeId,
            @Param("status") FurnitureScheme.SchemeStatus status
    );

    List<FurnitureScheme> findByRoomOrderBySchemeVersionAsc(HouseRoom room);
}
