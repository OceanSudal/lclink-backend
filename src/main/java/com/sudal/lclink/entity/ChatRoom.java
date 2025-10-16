package com.sudal.lclink.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roomId;

    // 이 채팅방의 소유자 (채팅방을 보는 사용자)
    private String userId;

    // 대화 상대방
    private String partnerId;

    @Transient
    private Integer partnerRoomId;

    private String partnerName;
    private String partnerAvatarUrl;

    private String lastMessage;
    private Instant lastMessageTimestamp;

    // 이 사용자가 읽지 않은 메시지 수
    private int unreadCount;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ChatMessage> messages;
}