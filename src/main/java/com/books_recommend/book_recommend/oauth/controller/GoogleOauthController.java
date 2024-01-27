package com.books_recommend.book_recommend.oauth.controller;

import com.books_recommend.book_recommend.oauth.service.GoogleOauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(("/members/oauth-login"))
@RequiredArgsConstructor
class GoogleOauthController {

    private final GoogleOauthService service;

    @GetMapping("/{registrationId}")
    public void loginGoogleOauth(@RequestParam String code,
                                        @PathVariable String registrationId){
        service.login(code, registrationId);

    }

}
