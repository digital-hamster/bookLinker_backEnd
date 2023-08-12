package com.books_recommend.book_recommend.service;

import com.books_recommend.book_recommend.dto.MemberDto;
import com.books_recommend.book_recommend.entity.Member;
import com.books_recommend.book_recommend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository repository;

    public MemberDto createMember(Requirement requirement){
        Member member = requirement.toEntity();
        Member saveMember = repository.save(member);

        return MemberDto.fromEntity(saveMember);
    }

    public record Requirement(
            String email,
            String nickname,
            String password
    ){
        public Member toEntity(){
            return new Member(email, nickname, password);
        }
    }
}
