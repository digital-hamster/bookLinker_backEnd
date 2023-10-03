package com.books_recommend.book_recommend.controller;

import com.books_recommend.book_recommend.common.web.ApiResponse;
import com.books_recommend.book_recommend.dto.MemberDto;
import com.books_recommend.book_recommend.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
class MemberController {
    private final MemberService service;

    @PostMapping
    ApiResponse<Response> createMember(@RequestBody @Valid Request request){
        MemberDto memberDto = service.createMember(request.toRequirement());

        Response response =  new Response(memberDto.id());
        return ApiResponse.success(response);
    }

    record Request(
            @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
                , message = "올바른 이메일 형식이 아닙니다.")
            String email,
            @NotBlank(message = "nickname는 필수입니다.")
            String nickname,
            @NotBlank(message = "password는 필수입니다.")
            String password
    ) {
        public MemberService.Requirement toRequirement(){
            return new MemberService.Requirement(email,
                    nickname,
                    password);
        }
    }
    record Response(
            Long id
    ){}

    @PutMapping("/update")
    ApiResponse updateMember(@RequestBody PutRequest putRequest){
        service.updateMember(
            new MemberService.PutRequirement(
                putRequest.nickname
            )
        );
        return ApiResponse.success();
    }

    record PutRequest(
        String nickname
    ){}
}