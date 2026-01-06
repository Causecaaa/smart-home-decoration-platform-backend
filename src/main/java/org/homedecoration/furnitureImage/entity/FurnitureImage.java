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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "scheme_id", nullable = false)
    private FurnitureScheme scheme;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
