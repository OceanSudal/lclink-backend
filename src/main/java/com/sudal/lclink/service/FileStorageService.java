package com.sudal.lclink.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private final Path uploadDir = Paths.get("uploads");

    public FileStorageService() {
        try {
            Files.createDirectories(uploadDir);
            log.info("업로드 디렉토리 생성: {}", uploadDir.toAbsolutePath());
        } catch (IOException e) {
            log.error("업로드 디렉토리 생성 실패", e);
        }
    }

    /**
     * MultipartFile을 저장 (HTTP 업로드용)
     * @param file MultipartFile
     * @return 저장된 파일명
     */
    public String storeMultipartFile(org.springframework.web.multipart.MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다");
        }

        String originalFileName = file.getOriginalFilename();
        String extension = getFileExtension(originalFileName);

        // PDF만 허용
        if (!extension.equalsIgnoreCase(".pdf")) {
            throw new IllegalArgumentException("PDF 파일만 업로드 가능합니다");
        }

        // 파일 크기 체크 (50MB 제한)
        if (file.getSize() > 50 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 50MB를 초과할 수 없습니다");
        }

        // 고유한 파일명 생성
        String savedFileName = UUID.randomUUID().toString() + extension;
        Path targetLocation = uploadDir.resolve(savedFileName);

        // 파일 저장
        Files.copy(file.getInputStream(), targetLocation);
        log.info("파일 저장 완료: {} ({}bytes)", savedFileName, file.getSize());

        return savedFileName;
    }

    /**
     * Base64 인코딩된 파일 데이터를 디코딩하여 파일로 저장 (WebSocket용 - 작은 파일만)
     * @param base64Data Base64 인코딩된 파일 데이터
     * @param originalFileName 원본 파일명
     * @return 저장된 파일명
     */
    public String storeFile(String base64Data, String originalFileName) throws IOException {
        if (base64Data == null || base64Data.isBlank()) {
            throw new IllegalArgumentException("파일 데이터가 없습니다");
        }

        // 파일 확장자 검증 (PDF만 허용)
        String extension = getFileExtension(originalFileName);
        if (!extension.equalsIgnoreCase(".pdf")) {
            throw new IllegalArgumentException("PDF 파일만 업로드 가능합니다");
        }

        try {
            // Base64 디코딩
            byte[] fileBytes = Base64.getDecoder().decode(base64Data);

            // 파일 크기 체크 (예: 10MB 제한)
            if (fileBytes.length > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("파일 크기는 10MB를 초과할 수 없습니다");
            }

            // 고유한 파일명 생성
            String savedFileName = UUID.randomUUID().toString() + extension;
            Path targetLocation = uploadDir.resolve(savedFileName);

            // 파일 저장
            Files.write(targetLocation, fileBytes);
            log.info("파일 저장 완료: {} ({}bytes)", savedFileName, fileBytes.length);

            return savedFileName;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("파일 저장 중 오류 발생", e);
            throw new IOException("파일 저장 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 파일명에서 확장자 추출
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return ".pdf"; // 기본값
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}