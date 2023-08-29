package com.books_recommend.book_recommend.controller;

import com.books_recommend.book_recommend.common.web.ApiResponse;
import com.books_recommend.book_recommend.dto.KakaoBookDto;
import com.books_recommend.book_recommend.service.KakaoApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
class KakaoApiController {

    private final KakaoApiService kakaoApiService;

    @Autowired
    public KakaoApiController(KakaoApiService kakaoApiService) {
        this.kakaoApiService = kakaoApiService;
    }

    @GetMapping("/kakao")
    public ApiResponse<List<Response>> callApi(@RequestParam String query) {
        List<KakaoBookDto> savedBooks = kakaoApiService.callApiAndSaveToDatabase(query);
        List<Response> responseList = savedBooks.stream()
            .map(bookDto -> new Response(
                bookDto.title(),
                bookDto.authors(),
                bookDto.isbn(),
                bookDto.publisher(),
                bookDto.thumbnail(),
                bookDto.url()
            ))
            .collect(Collectors.toList());
        return ApiResponse.success(responseList);
    }
    record Response(
        String title,
        String authors,
        String isbn,
        String publisher,
        String thumbnail, //image
        String url
    ){}
}
