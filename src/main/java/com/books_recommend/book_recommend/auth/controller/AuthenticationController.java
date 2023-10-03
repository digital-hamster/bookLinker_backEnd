package com.books_recommend.book_recommend.auth.controller;

import com.books_recommend.book_recommend.auth.service.AuthenticationService;
import com.books_recommend.book_recommend.common.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(("/members/authenticate"))
@CrossOrigin
@RequiredArgsConstructor
class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping
    public ApiResponse<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest request){
        var requirement = new AuthenticationService.Requirement(
            request.email,
            request.password
        );

        var authenticateInfo = service.authenticate(requirement);

        var response = new JwtResponse(
            authenticateInfo.token()
        );

        return ApiResponse.success(response);
    }

    record JwtRequest(
        String email,
        String password
    ){}
    record JwtResponse(
        String token
    ){}

}
