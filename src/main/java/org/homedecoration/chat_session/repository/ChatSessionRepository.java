package org.homedecoration.chat_session.repository;

import org.homedecoration.chat_session.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Optional<ChatSession> findByUserIdAndPartnerId(Long userId, Long partnerId);

    List<ChatSession> findByUserIdOrderByLastMessageTimeDesc(Long userId);
}
