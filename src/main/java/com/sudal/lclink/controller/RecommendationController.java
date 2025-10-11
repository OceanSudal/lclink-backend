package com.sudal.lclink.controller;

import com.sudal.lclink.dto.CargoItemDto;
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
    public List<CargoItemDto> getAiRecommendations() {
        System.out.println("===========================================");
        System.out.println("🌐 /api/recommendations/ai 엔드포인트 호출됨");
        System.out.println("===========================================");

        // TODO: Spring Security 적용 후 실제 로그인된 사용자 ID를 가져와야 합니다.
        String currentForwarderId = "test_forwarder";

        List<CargoItemDto> result = recommendationService.getAiRecommendations(currentForwarderId);

        System.out.println("===========================================");
        System.out.println("🌐 Controller 응답: " + result.size() + "개 아이템");
        System.out.println("===========================================");

        return result;
    }
}