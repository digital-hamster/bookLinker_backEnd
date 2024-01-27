package com.books_recommend.book_recommend.oauth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleOauthService {

    public void login(String code,
                      String registrationId){
        System.out.println("code "+code);
        System.out.println("registrationId "+registrationId);
    }

}
