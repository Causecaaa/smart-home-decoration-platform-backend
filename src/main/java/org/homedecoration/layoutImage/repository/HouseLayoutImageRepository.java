package org.homedecoration.layoutImage.repository;

import org.homedecoration.layoutImage.entity.HouseLayoutImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HouseLayoutImageRepository extends JpaRepository<HouseLayoutImage, Long> {
    // 根据布局查图片列表
    List<HouseLayoutImage> findByLayout_Id(Long layoutId);

    // 可按图片类型查
    HouseLayoutImage findByLayoutIdAndImageType(Long layoutId, HouseLayoutImage.ImageType imageType);

}
