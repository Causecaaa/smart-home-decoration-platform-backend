package org.homedecoration.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

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

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "layout_id", nullable = false)
    private HouseLayout layout;

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

    @Column(name = "notes")
    private String notes;

}