package org.homedecoration.furnitureScheme.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.homedecoration.furnitureImage.entity.FurnitureImage;
import org.homedecoration.houseRoom.entity.HouseRoom;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "furniture_scheme")
public class FurnitureScheme {

    public enum SchemeStatus {
        DRAFT,
        SUBMITTED,
        CONFIRMED,
        ARCHIVED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheme_id", nullable = false)
    private Long id;

    /**
     * 所属房间（一个房间可有多轮方案）
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "room_id", nullable = false)
    private HouseRoom room;

    @Column(name = "designer_id", nullable = false)
    private Long designerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "scheme_status", length = 20)
    private SchemeStatus schemeStatus = SchemeStatus.SUBMITTED;

    @Column(name = "scheme_version", length = 20)
    private Integer schemeVersion; // v1 / v2 / v3

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
