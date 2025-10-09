package com.sudal.lclink.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudal.lclink.dto.MessageDto;
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
        msg.setMessage(msg.getSenderId() + "님 접속");
        sendJsonMessage(session, msg);
    }

    private void handleTalk(WebSocketSession senderSession, MessageDto msg) {
        if (msg.getReceiverId() == null || msg.getReceiverId().isBlank()) {
            sendErrorMessage(senderSession, "receiverId 필요");
            return;
        }

        WebSocketSession target = userSessionMap.get(msg.getReceiverId());
        if (target == null || !target.isOpen()) {
            sendErrorMessage(senderSession, "수신자 연결 안됨");
            return;
        }

        try {
            // 방법1: HTTP로 이미 업로드된 파일 (fileUrl만 있음)
            if (msg.isHasFile() && msg.getFileUrl() != null) {
                // fileUrl과 fileName만 전달 (이미 서버에 저장됨)
                msg.setFileData(null); // Base64 데이터 제거
                log.info("파일 알림 전송: {}", msg.getFileName());
            }
            // 방법2: WebSocket으로 직접 전송 (작은 파일만, Base64)
            else if (msg.isHasFile() && msg.getFileData() != null && !msg.getFileData().isBlank()) {
                String savedFileName = fileStorageService.storeFile(
                        msg.getFileData(),
                        msg.getFileName()
                );
                msg.setFileName(savedFileName);
                msg.setFileUrl("/files/" + savedFileName);
                msg.setFileData(null); // Base64 제거
                msg.setMessage("PDF 파일: " + msg.getFileName());
                log.info("WebSocket 파일 저장: {}", savedFileName);
            }

            // 수신자와 발신자에게 메시지 전송
            sendJsonMessage(target, msg);
            // sendJsonMessage(senderSession, msg);

        } catch (Exception e) {
            log.error("TALK 처리 오류", e);
            sendErrorMessage(senderSession, "메시지 전송 실패: " + e.getMessage());
        }
    }

    private void handleLeave(WebSocketSession session, MessageDto msg) {
        if (msg.getSenderId() != null) userSessionMap.remove(msg.getSenderId());
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