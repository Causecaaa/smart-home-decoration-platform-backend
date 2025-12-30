package org.homedecoration.entity;

import jakarta.persistence.*;
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
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "layout_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "house_id", nullable = false)
    private House house;

    @Column(name = "layout_intent", length = 20)
    private String layoutIntent;

    @Column(name = "redesign_notes")
    private String redesignNotes;

    @Column(name = "layout_version", length = 20)
    private String layoutVersion;

    @Column(name = "layout_status", length = 20)
    private String layoutStatus;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}