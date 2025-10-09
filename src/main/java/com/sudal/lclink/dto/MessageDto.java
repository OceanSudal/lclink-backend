package com.sudal.lclink.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {

    public enum MessageType { JOIN, TALK, LEAVE, ERROR }

    private MessageType messageType;
    private String senderId;
    private String receiverId;
    private String message;

    private boolean hasFile = false;
    private String fileName;
    private String fileUrl;
    private String fileData; // Base64 (작은 파일용, 선택사항)
    private Long fileSize; // 파일 크기 (bytes)
    private String timestamp;
}