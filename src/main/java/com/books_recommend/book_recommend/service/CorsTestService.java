package com.books_recommend.book_recommend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CorsTestService {
    private final MemberService memberService;

    public String test(){
        return "post success";
    }

    public String testWithHeader(){
        var member = memberService.findMember();
        return member.getNickName();
    }
}
