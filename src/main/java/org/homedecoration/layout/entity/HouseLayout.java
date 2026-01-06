package org.homedecoration.layout.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.homedecoration.house.entity.House;

import java.time.Instant;


@Getter
@Setter
@Entity
@Table(name = "house_layout")
public class HouseLayout {
    public enum LayoutIntent {
        KEEP_ORIGINAL,
        REDESIGN
    }
    public enum LayoutStatus {
        DRAFT,          // 草稿（用户刚创建 / 设计师未完成）
        SUBMITTED,      // 设计师已提交方案（待用户选择）
        CONFIRMED,       // 用户已确认的最终方案
        ROOMS_CONFIRMED   // 房间 结构已确认
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "layout_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    @JoinColumn(name = "designer_id", nullable = true)
    private Long designerId;


    @Enumerated(EnumType.STRING)
    @Column(name = "layout_intent", nullable = false, length = 20)
    private LayoutIntent layoutIntent;

    @Column(name = "redesign_notes")
    private String redesignNotes;

    @Column(name = "layout_version", length = 20)
    private Integer layoutVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "layout_status", nullable = false, length = 20)
    private LayoutStatus layoutStatus;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

}