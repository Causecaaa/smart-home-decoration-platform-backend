package org.homedecoration.post.image.repository;

import org.homedecoration.post.image.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    List<PostImage> findByPostIdOrderByCreatedAtAsc(Long postId);

    void deleteByPostId(Long postId);

    List<PostImage> findTop3ByPostIdOrderByCreatedAtAsc(Long postId);

}
