package org.homedecoration.chat_message.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "chat_message")
public class ChatMessage {
    public enum ContentType {
        TEXT,
        IMAGE,
        FILE,
        LINK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 消息ID

    @Column(name = "sender_id", nullable = false)
    private Long senderId; // 发送者用户ID

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId; // 接收者用户ID

    @Column(name = "content", nullable = false, length = 1000)
    private String content; // 消息内容

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 20)
    private ContentType contentType = ContentType.TEXT; // 消息类型：TEXT / IMAGE / FILE / LINK

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp; // 发送时间

}
