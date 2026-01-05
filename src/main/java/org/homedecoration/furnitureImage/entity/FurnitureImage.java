package org.homedecoration.furnitureImage.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.homedecoration.furnitureScheme.entity.FurnitureScheme;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "furniture_image")
public class FurnitureImage {

    public enum ImageType {
        DESIGN,
        CONFIRMED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Long id;

    /**
     * 所属家具设计方案
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "scheme_id", nullable = false)
    private FurnitureScheme scheme;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "image_type", length = 20)
    private ImageType imageType = ImageType.DESIGN;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
