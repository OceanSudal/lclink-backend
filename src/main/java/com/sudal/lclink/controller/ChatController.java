package com.sudal.lclink.controller;

import com.sudal.lclink.entity.ChatMessage;
import com.sudal.lclink.entity.ChatRoom;
import com.sudal.lclink.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 내 채팅방 목록 조회
     */
    @GetMapping("/rooms/all")
    public ResponseEntity<List<ChatRoom>> getAllRooms() {
        return ResponseEntity.ok(chatService.getAllChatRooms());
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> getRooms(@RequestParam String userId) {
        return ResponseEntity.ok(chatService.getChatRooms(userId));
    }

    /**
     * 특정 채팅방의 메시지 목록 조회
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Integer roomId) {
        return ResponseEntity.ok(chatService.getMessages(roomId));
    }

    /**
     * 채팅방 생성 또는 기존 채팅방 조회
     */
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoom> createRoom(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String partnerId = body.get("partnerId");
        String partnerName = body.get("partnerName");
        String partnerAvatarUrl = body.get("partnerAvatarUrl");
        return ResponseEntity.ok(chatService.createOrGetRoom(userId, partnerId, partnerName, partnerAvatarUrl));
    }

    /**
     * 메시지 읽음 처리
     */
    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Integer roomId,
            @RequestParam String userId) {
        chatService.markAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }
}