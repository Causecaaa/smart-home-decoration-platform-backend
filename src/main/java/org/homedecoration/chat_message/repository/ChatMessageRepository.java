package org.homedecoration.chat_message.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.homedecoration.chat_message.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 查询两个用户之间的聊天记录（双向）
     */
    List<ChatMessage> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
            Long senderId1, Long receiverId1,
            Long senderId2, Long receiverId2
    );

    Optional<ChatMessage> findById(Long id);

    @Query("SELECT DISTINCT CASE " +
            "WHEN cm.senderId = :userId THEN cm.receiverId " +
            "ELSE cm.senderId END AS otherUserId, " +
            "MAX(cm.timestamp) as latestTime " +
            "FROM ChatMessage cm " +
            "WHERE cm.senderId = :userId OR cm.receiverId = :userId " +
            "GROUP BY otherUserId " +
            "ORDER BY latestTime DESC")
    List<Object[]> findDistinctChatUserIdsByUserId(@Param("userId") Long userId);


    // 查找两个人会话的最后一条消息
    Optional<ChatMessage> findTopBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampDesc(
            Long senderId, Long receiverId,
            Long senderId2, Long receiverId2
    );



}
