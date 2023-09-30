package com.books_recommend.book_recommend.auth.controller;

import com.books_recommend.book_recommend.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequiredArgsConstructor
class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/members/authenticate")
    public ResponseEntity createAuthenticationToken(@RequestBody JwtRequest request){
        var requirement = new AuthenticationService.Requirement(
            request.email,
            request.password
        );

        var authenticateInfo = service.authenticate(requirement);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + authenticateInfo.token());

        return ResponseEntity
            .ok()
            .headers(headers)
            .body(null);
    }

    record JwtRequest(
        String email,
        String password
    ){}
    record JwtResponse(
        String token
    ){}

}
