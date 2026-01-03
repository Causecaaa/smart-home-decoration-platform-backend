package org.homedecoration.furnitureScheme.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.homedecoration.layout.entity.HouseLayout;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "furniture_scheme")
public class FurnitureScheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scheme_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "layout_id", nullable = false)
    private HouseLayout layout;

    @Column(name = "scheme_status", length = 20)
    private String schemeStatus;

    @Column(name = "scheme_version", length = 20)
    private String schemeVersion;

    @Column(name = "confirmed_furniture_image_id")
    private Long confirmedFurnitureImageId;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

}