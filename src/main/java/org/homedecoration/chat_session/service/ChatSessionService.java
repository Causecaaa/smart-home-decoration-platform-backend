package org.homedecoration.chat_session.service;

import org.homedecoration.chat_session.entity.ChatSession;
import org.homedecoration.chat_session.repository.ChatSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatSessionService {

    private final ChatSessionRepository chatSessionRepository;

    public ChatSessionService(ChatSessionRepository chatSessionRepository) {
        this.chatSessionRepository = chatSessionRepository;
    }

    /**
     * 发送消息时更新双方会话状态
     */
    @Transactional
    public void updateOnSend(Long senderId, Long receiverId, String content, String type, LocalDateTime time) {
        // 发送方视角（已读）
        updateOrCreateSession(senderId, receiverId, content, type, time, true);

        // 接收方视角（未读）
        updateOrCreateSession(receiverId, senderId, content, type, time, false);
    }

    /**
     * 用户进入聊天页，标记会话为已读
     */
    @Transactional
    public void markRead(Long userId, Long partnerId) {
        ChatSession session = chatSessionRepository.findByUserIdAndPartnerId(userId, partnerId).orElse(null);
        if (session != null) {
            session.setLastReadTime(LocalDateTime.now());
            chatSessionRepository.save(session);
        }
    }

    /**
     * 查询用户的会话列表（按最后消息时间倒序）
     */
    public List<ChatSession> listSessionsByUser(Long userId) {
        return chatSessionRepository.findByUserIdOrderByLastMessageTimeDesc(userId);
    }

    /**
     * 判断是否有未读消息（小红点）
     */
    public boolean hasUnread(Long userId, Long partnerId) {
        ChatSession session = chatSessionRepository.findByUserIdAndPartnerId(userId, partnerId).orElse(null);
        if (session == null || session.getLastMessageTime() == null) {
            return false;
        }
        if (session.getLastReadTime() == null) {
            return true;
        }
        return session.getLastMessageTime().isAfter(session.getLastReadTime());
    }

    /**
     * 内部方法：存在就更新，不存在就创建会话
     */
    private void updateOrCreateSession(Long userId, Long partnerId, String content, String type, LocalDateTime time, boolean markRead) {
        ChatSession session = chatSessionRepository.findByUserIdAndPartnerId(userId, partnerId).orElse(null);

        if (session == null) {
            ChatSession newSession = new ChatSession();
            newSession.setUserId(userId);
            newSession.setPartnerId(partnerId);
            newSession.setLastMessageContent(content);
            newSession.setLastMessageType(type);
            newSession.setLastMessageTime(time);

            if (markRead) {
                newSession.setLastReadTime(time);
            }

            chatSessionRepository.save(newSession);
        } else {
            session.setLastMessageContent(content);
            session.setLastMessageType(type);
            session.setLastMessageTime(time);

            if (markRead) {
                session.setLastReadTime(time);
            }

            chatSessionRepository.save(session);
        }
    }
}
