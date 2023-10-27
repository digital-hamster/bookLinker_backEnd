package com.books_recommend.book_recommend.controller;

import com.books_recommend.book_recommend.common.web.ApiResponse;
import com.books_recommend.book_recommend.service.CorsTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
class CorsTestController {
    private final CorsTestService service;

    //헤더를 안받음
    @PostMapping("/unheaders")
    ApiResponse<String> test(){
        var response = service.test();

        return ApiResponse.success("(헤더X) post 테스트 성공 " + response);
    }

    //헤더를 받음
    @PostMapping("/headers")
    ApiResponse<String> testWithHeader(){
        var response = service.testWithHeader();

        return ApiResponse.success(
            "(헤더o) post 테스트 성공 " +
                " 로그인한 유저의 닉네임: " +
                "[ " + response + " ]");
    }

}
