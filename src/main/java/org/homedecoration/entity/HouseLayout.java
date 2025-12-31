package org.homedecoration.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
        DRAFT,
        CONFIRMED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "layout_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    @Enumerated(EnumType.STRING)
    @Column(name = "layout_intent", nullable = false, length = 20)
    private LayoutIntent layoutIntent;

    @Column(name = "redesign_notes")
    private String redesignNotes;

    @Column(name = "layout_version", length = 20)
    private String layoutVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "layout_status", nullable = false, length = 20)
    private LayoutStatus layoutStatus;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}