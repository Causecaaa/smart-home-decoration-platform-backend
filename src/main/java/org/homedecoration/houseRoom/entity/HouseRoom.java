package org.homedecoration.houseRoom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.homedecoration.furniture.furnitureScheme.entity.FurnitureScheme;
import org.homedecoration.layout.entity.HouseLayout;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "house_room")
public class HouseRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", nullable = false)
    private Long id;

    @Column(name = "floor_no")
    private Integer floorNo;

    /**
     * 所属布局（仅在 Layout = CONFIRMED 后才有效）
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "layout_id", nullable = false)
    private HouseLayout layout;

    @Column(name = "designer_id", nullable = false)
    private Long designerId; // 谁上传的房间方案

    @Column(name = "room_type", length = 20)
    private String roomType;

    @Column(name = "room_name", length = 50)
    private String roomName;

    @Column(name = "area", precision = 6, scale = 2)
    private BigDecimal area;

    @Column(name = "has_window")
    private Boolean hasWindow;

    @Column(name = "has_balcony")
    private Boolean hasBalcony;

    @Column(name = "notes", length = 255)
    private String notes;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<FurnitureScheme> schemes;
}
