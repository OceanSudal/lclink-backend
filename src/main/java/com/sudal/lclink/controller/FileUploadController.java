package com.sudal.lclink.controller;

import com.sudal.lclink.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;

    /**
     * HTTP POST로 파일 업로드
     * 업로드 후 파일 정보 반환 -> 클라이언트가 WebSocket으로 파일 정보만 전송
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "파일이 비어있습니다"));
            }

            // 파일 저장
            String savedFileName = fileStorageService.storeMultipartFile(file);
            String fileUrl = "/files/" + savedFileName;

            log.info("파일 업로드 성공: {} -> {}", file.getOriginalFilename(), savedFileName);

            Map<String, String> response = new HashMap<>();
            response.put("success", "true");
            response.put("originalFileName", file.getOriginalFilename());
            response.put("savedFileName", savedFileName);
            response.put("fileUrl", fileUrl);
            response.put("fileSize", String.valueOf(file.getSize()));

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("파일 검증 실패", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("파일 업로드 실패", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "파일 업로드 중 오류가 발생했습니다"));
        }
    }
}