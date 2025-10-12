package com.sudal.lclink.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudal.lclink.dto.MessageDto;
import com.sudal.lclink.entity.ChatMessage;
import com.sudal.lclink.entity.ChatRoom;
import com.sudal.lclink.service.ChatService;
import com.sudal.lclink.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final FileStorageService fileStorageService;
    private final ChatService chatService;

    private final Map<String, WebSocketSession> userSessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket 연결됨: {}", session.getId());
        session.sendMessage(new TextMessage("WebSocket 연결 완료"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            MessageDto msg = mapper.readValue(message.getPayload(), MessageDto.class);
            msg.setTimestamp(Instant.now().atOffset(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

            switch (msg.getMessageType()) {
                case JOIN -> handleJoin(session, msg);
                case TALK -> handleTalk(session, msg);
                case LEAVE -> handleLeave(session, msg);
                default -> sendErrorMessage(session, "알 수 없는 메시지 타입입니다.");
            }
        } catch (Exception e) {
            log.error("메시지 처리 중 예외", e);
            sendErrorMessage(session, "서버 내부 오류");
        }
    }

    private void handleJoin(WebSocketSession session, MessageDto msg) {
        if (msg.getSenderId() == null || msg.getSenderId().isBlank()) {
            sendErrorMessage(session, "senderId 필요");
            return;
        }
        userSessionMap.put(msg.getSenderId(), session);

        // 읽음 처리
        if (msg.getRoomId() != null) {
            chatService.markAsRead(msg.getRoomId(), msg.getSenderId());
        }

        log.info("사용자 접속: {} (세션: {})", msg.getSenderId(), session.getId());
    }

    private void handleTalk(WebSocketSession senderSession, MessageDto msg) {
        if (msg.getReceiverId() == null || msg.getReceiverId().isBlank()) {
            sendErrorMessage(senderSession, "receiverId 필요");
            return;
        }

        try {
            // 파일 처리
            if (msg.isHasFile() && msg.getFileData() != null && !msg.getFileData().isBlank()) {
                String savedFileName = fileStorageService.storeFile(msg.getFileData(), msg.getFileName());
                msg.setFileName(savedFileName);
                msg.setFileUrl("/files/" + savedFileName);
                msg.setFileData(null);
                msg.setMessage("파일: " + msg.getFileName());
            }

            // DB 저장
            ChatRoom room = new ChatRoom();
            room.setRoomId(msg.getRoomId());
            ChatMessage chatMessage = ChatMessage.builder()
                    .senderId(msg.getSenderId())
                    .receiverId(msg.getReceiverId())
                    .message(msg.getMessage())
                    .hasFile(msg.isHasFile())
                    .fileUrl(msg.getFileUrl())
                    .fileName(msg.getFileName())
                    //.chatRoom(room)
                    .build();

            ChatMessage savedMessage = chatService.saveMessage(chatMessage);
            msg.setTimestamp(savedMessage.getTimestamp().atOffset(ZoneOffset.UTC)
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            msg.setRoomId(savedMessage.getChatRoom().getRoomId());

            // 🔥 수신자에게 전송
            WebSocketSession receiverSession = userSessionMap.get(msg.getReceiverId());
            if (receiverSession != null && receiverSession.isOpen()) {
                sendJsonMessage(receiverSession, msg);
            } else {
                log.warn("수신자 {} 오프라인", msg.getReceiverId());
            }

            // 🔥 발신자에게도 전송 (본인 화면에 표시)
            sendJsonMessage(senderSession, msg);

        } catch (Exception e) {
            log.error("TALK 처리 오류", e);
            sendErrorMessage(senderSession, "메시지 전송 실패: " + e.getMessage());
        }
    }

    private void handleLeave(WebSocketSession session, MessageDto msg) {
        if (msg.getSenderId() != null) {
            userSessionMap.remove(msg.getSenderId());
            log.info("사용자 퇴장: {}", msg.getSenderId());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 세션이 끊어질 때 맵에서 제거
        userSessionMap.entrySet().removeIf(entry -> entry.getValue().getId().equals(session.getId()));
        log.info("WebSocket 연결 종료: {} (상태: {})", session.getId(), status);
    }

    private void sendJsonMessage(WebSocketSession session, MessageDto msg) {
        if (session == null || !session.isOpen()) return;
        try {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(msg)));
        } catch (Exception e) {
            log.error("전송 실패", e);
        }
    }

    private void sendErrorMessage(WebSocketSession session, String errorMsg) {
        if (session == null || !session.isOpen()) return;
        try {
            MessageDto error = new MessageDto();
            error.setMessageType(MessageDto.MessageType.ERROR);
            error.setMessage(errorMsg);
            session.sendMessage(new TextMessage(mapper.writeValueAsString(error)));
        } catch (Exception e) {
            log.error("에러 전송 실패", e);
        }
    }
}