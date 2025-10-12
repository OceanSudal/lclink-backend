package com.sudal.lclink.service;

import com.sudal.lclink.entity.ChatMessage;
import com.sudal.lclink.entity.ChatRoom;
import com.sudal.lclink.repository.ChatMessageRepository;
import com.sudal.lclink.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public List<ChatRoom> getAllChatRooms() {
        return chatRoomRepository.findAll();
    }
    /**
     * 특정 사용자의 채팅방 목록 조회
     */
    public List<ChatRoom> getChatRooms(String userId) {
        return chatRoomRepository.findAllByUserId(userId);
    }

    /**
     * 특정 채팅방의 메시지 조회
     */
    public List<ChatMessage> getMessages(Integer roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방 없음"));
        return chatMessageRepository.findAllByChatRoomOrderByTimestampAsc(room);
    }

    /**
     * 채팅방 생성 또는 기존 채팅방 조회
     * 1:1 채팅이므로 양쪽 사용자 모두에게 채팅방 레코드 생성
     */
    @Transactional
    public ChatRoom createOrGetRoom(String userId, String partnerId, String partnerName, String partnerAvatarUrl) {
        // 내 입장에서의 채팅방 찾기
        Optional<ChatRoom> myRoom = chatRoomRepository.findByUserIdAndPartnerId(userId, partnerId);

        if (myRoom.isPresent()) {
            return myRoom.get();
        }

        // 새 채팅방 생성 - 양방향
        ChatRoom roomForMe = ChatRoom.builder()
                .userId(userId)
                .partnerId(partnerId)
                .partnerName(partnerName)
                .partnerAvatarUrl(partnerAvatarUrl)
                .lastMessage("")
                .lastMessageTimestamp(Instant.now())
                .unreadCount(0)
                .build();

        ChatRoom roomForPartner = ChatRoom.builder()
                .userId(partnerId)
                .partnerId(userId)
                .partnerName("나") // 실제로는 userId의 이름을 조회해야 함
                .partnerAvatarUrl(null) // 실제로는 userId의 아바타를 조회해야 함
                .lastMessage("")
                .lastMessageTimestamp(Instant.now())
                .unreadCount(0)
                .build();

        chatRoomRepository.save(roomForMe);
        chatRoomRepository.save(roomForPartner);

        return roomForMe;
    }

    /**
     * 특정 채팅방의 읽지 않은 메시지 읽음 처리
     */
    @Transactional
    public void markAsRead(Integer roomId, String userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방 없음"));

        // 해당 사용자의 채팅방인지 확인
        if (!room.getUserId().equals(userId)) {
            throw new RuntimeException("권한 없음");
        }

        room.setUnreadCount(0);
        chatRoomRepository.save(room);
    }

    /**
     * 메시지 저장 및 양쪽 채팅방 업데이트
     */
    @Transactional
    public ChatMessage saveMessage(ChatMessage msg) {
        // 발신자 입장의 채팅방
        ChatRoom senderRoom = chatRoomRepository.findByUserIdAndPartnerId(
                        msg.getSenderId(), msg.getReceiverId())
                .orElseThrow(() -> new RuntimeException("발신자 채팅방 없음"));

        // 수신자 입장의 채팅방
        ChatRoom receiverRoom = chatRoomRepository.findByUserIdAndPartnerId(
                        msg.getReceiverId(), msg.getSenderId())
                .orElseThrow(() -> new RuntimeException("수신자 채팅방 없음"));

        Instant now = Instant.now();

        // 발신자 채팅방 업데이트 (unreadCount 증가 안 함)
        senderRoom.setLastMessage(msg.getMessage());
        senderRoom.setLastMessageTimestamp(now);
        chatRoomRepository.save(senderRoom);

        // 수신자 채팅방 업데이트 (unreadCount 증가)
        receiverRoom.setLastMessage(msg.getMessage());
        receiverRoom.setLastMessageTimestamp(now);
        receiverRoom.setUnreadCount(receiverRoom.getUnreadCount() + 1);
        chatRoomRepository.save(receiverRoom);

        // 메시지 저장 (발신자 채팅방에 연결)
        msg.setChatRoom(senderRoom);
        msg.setTimestamp(now);
        return chatMessageRepository.save(msg);
    }
}