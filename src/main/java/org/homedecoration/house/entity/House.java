package org.homedecoration.house.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.homedecoration.user.entity.User;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "house")
public class House {
    public enum DecorationType {
        FULL,
        HALF,
        LOOSE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "house_id", nullable = false)
    private Long id;

    @NotNull(message = "房屋所属用户不能为空")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "城市不能为空")
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

    @NotNull(message = "建筑面积不能为空")
    @Column(name = "area", nullable = false, precision = 6, scale = 2)
    private BigDecimal area;

    @Column(name = "layout_type", length = 50)
    private String layoutType;

    @Column(name = "floor_count")
    private Integer floorCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "decoration_type", nullable = false, length = 20)
    private DecorationType decorationType;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

}