package org.homedecoration.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "house")
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "house_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private org.homedecoration.entity.User user;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "community_name", length = 100)
    private String communityName;

    @Column(name = "building_no", length = 20)
    private String buildingNo;

    @Column(name = "unit_no", length = 20)
    private String unitNo;

    @Column(name = "room_no", length = 20)
    private String roomNo;

    @Column(name = "area", nullable = false, precision = 6, scale = 2)
    private BigDecimal area;

    @Column(name = "layout_type", length = 50)
    private String layoutType;

    @Column(name = "floor_count")
    private Integer floorCount;

    @Column(name = "decoration_type", length = 20)
    private String decorationType;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}