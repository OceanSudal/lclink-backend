package com.sudal.lclink.controller;

import com.sudal.lclink.dto.CargoRequestDto; // 👈 CargoItemDto 대신 CargoRequestDto를 import
import com.sudal.lclink.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/ai")
    // 👈 반환 타입을 List<CargoItemDto>에서 List<CargoRequestDto>로 변경
    public List<CargoRequestDto> getAiRecommendations() {
        // TODO: Spring Security 적용 후 실제 로그인된 사용자 ID를 가져와야 합니다.
        String currentForwarderId = "test_forwarder";

        // 👈 서비스 메서드 호출 시 반환 타입도 List<CargoRequestDto>로 변경
        List<CargoRequestDto> result = recommendationService.getAiRecommendations(currentForwarderId);

        return result;
    }
}