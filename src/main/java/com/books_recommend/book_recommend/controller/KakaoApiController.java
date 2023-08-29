package com.books_recommend.book_recommend.controller;

import com.books_recommend.book_recommend.service.KakaoApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
class KakaoApiController {

    private final KakaoApiService kakaoApiService;

    @Autowired
    public KakaoApiController(KakaoApiService kakaoApiService) {
        this.kakaoApiService = kakaoApiService;
    }

    @GetMapping("/kakao")
    public Map<String, Object> callApi(@RequestParam String query) {
        return kakaoApiService.callApi(query);
    }
}
