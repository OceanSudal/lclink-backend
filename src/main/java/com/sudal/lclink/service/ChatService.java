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
        List<ChatRoom> rooms = chatRoomRepository.findAllByUserId(userId);

        for (ChatRoom room : rooms){
            Optional<ChatRoom> partnerRoom = chatRoomRepository
                    .findByUserIdAndPartnerId(room.getPartnerId(), room.getUserId());

            if (partnerRoom.isPresent()) {
                room.setPartnerRoomId(partnerRoom.get().getRoomId());
            }
        }

        return rooms;
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
        // ID 정렬 (A가 B보다 ID가 빠르다고 가정)
        String first = userId.compareTo(partnerId) < 0 ? userId : partnerId; // A
        String second = userId.compareTo(partnerId) < 0 ? partnerId : userId; // B

        // 1. 논리적 채팅방 찾기 (항상 first와 second로 찾음)
        Optional<ChatRoom> logicalRoomOptional = chatRoomRepository.findByUserIdAndPartnerId(first, second);

        if (logicalRoomOptional.isPresent()) {
            // 이미 방이 있다면, '논리적 방'을 반환
            return logicalRoomOptional.get();
        }

        // 새 채팅방 생성 - 양방향
        Instant now = Instant.now();

        // 2. 논리적 방 (first 사용자 관점의 방)
        ChatRoom logicalRoom = ChatRoom.builder()
                .userId(first)
                .partnerId(second)
                .partnerName(userId.equals(first) ? partnerName : null) // first가 userId면 partnerName 사용
                .partnerAvatarUrl(userId.equals(first) ? partnerAvatarUrl : null)
                .lastMessage("")
                .lastMessageTimestamp(now)
                .unreadCount(0)
                .build();
        chatRoomRepository.save(logicalRoom); // logicalRoom.roomId가 생성됨

        // 3. 상대방 방 (second 사용자 관점의 방)
        ChatRoom partnerRoom = ChatRoom.builder()
                .userId(second)
                .partnerId(first)
                .partnerName(userId.equals(second) ? partnerName : null) // second가 userId면 partnerName 사용
                .partnerAvatarUrl(userId.equals(second) ? partnerAvatarUrl : null)
                .lastMessage("")
                .lastMessageTimestamp(now)
                .unreadCount(0)
                .build();
        chatRoomRepository.save(partnerRoom);

        // 클라이언트에게는 항상 '논리적 방'을 반환
        return logicalRoom;
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
    // ChatService.java

    /**
     * 메시지 저장 및 양쪽 채팅방 업데이트
     */
    @Transactional
    public ChatMessage saveMessage(ChatMessage msg) {
        Instant now = Instant.now();

        // 1. 논리적 채팅방 조회 (A, B 두 사용자 중 ID가 빠른 사용자의 ChatRoom)
        String first = msg.getSenderId().compareTo(msg.getReceiverId()) < 0 ? msg.getSenderId() : msg.getReceiverId();
        String second = msg.getSenderId().compareTo(msg.getReceiverId()) < 0 ? msg.getReceiverId() : msg.getSenderId();

        ChatRoom logicalRoom = chatRoomRepository.findByUserIdAndPartnerId(first, second)
                .orElseThrow(() -> new RuntimeException("논리적 채팅방 없음"));

        // 2. 수신자 관점의 채팅방 조회 및 unreadCount 증가
        ChatRoom receiverRoom = chatRoomRepository.findByUserIdAndPartnerId(msg.getReceiverId(), msg.getSenderId())
                .orElseThrow(() -> new RuntimeException("수신자 채팅방 없음"));

        receiverRoom.setLastMessage(msg.getMessage());
        receiverRoom.setLastMessageTimestamp(now);
        receiverRoom.setUnreadCount(receiverRoom.getUnreadCount() + 1); // 수신자 unreadCount 증가
        chatRoomRepository.save(receiverRoom);

        // 3. 발신자 관점의 채팅방 LastMessage 업데이트 (unreadCount는 0으로 유지)
        ChatRoom senderRoom = chatRoomRepository.findByUserIdAndPartnerId(msg.getSenderId(), msg.getReceiverId())
                .orElseThrow(() -> new RuntimeException("발신자 채팅방 없음"));

        senderRoom.setLastMessage(msg.getMessage());
        senderRoom.setLastMessageTimestamp(now);
        chatRoomRepository.save(senderRoom);

        // 4. ChatMessage 저장 (논리적 방에 연결)
        msg.setChatRoom(logicalRoom); // 🚨 모든 메시지는 이 논리적 방에 연결됨
        msg.setTimestamp(now);
        return chatMessageRepository.save(msg);
    }
}