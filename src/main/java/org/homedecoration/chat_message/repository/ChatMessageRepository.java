package org.homedecoration.chat_message.repository;

import org.homedecoration.chat_message.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * 查询两个用户之间的聊天记录（双向）
     */
    List<ChatMessage> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
            Long senderId1, Long receiverId1,
            Long senderId2, Long receiverId2
    );
}
