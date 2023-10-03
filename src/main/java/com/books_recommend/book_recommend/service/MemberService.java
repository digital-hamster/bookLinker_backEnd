package com.books_recommend.book_recommend.service;

import com.books_recommend.book_recommend.auth.util.SecurityUtil;
import com.books_recommend.book_recommend.common.exception.BusinessLogicException;
import com.books_recommend.book_recommend.common.exception.ExceptionCode;
import com.books_recommend.book_recommend.dto.MemberDto;
import com.books_recommend.book_recommend.entity.Member;
import com.books_recommend.book_recommend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository repository;
    private final PasswordEncoder passwordEncoder;

    public Member findMember(){
        var email = SecurityUtil.getMemberEmail();
        var member = repository.findByEmail(email)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        return member;
    }

    public MemberDto createMember(Requirement requirement){
        var encodedPassword = passwordEncoder.encode(requirement.password);
        emailVerify(requirement.email, repository);

        var member = new Member(
            requirement.email,
            requirement.nickname,
            encodedPassword
        );
        Member saveMember = repository.save(member);

        return MemberDto.fromEntity(saveMember);
    }

    private static void emailVerify(String email, MemberRepository repository){
        if(repository.existsByEmail(email)){
            throw new BusinessLogicException(ExceptionCode.EMAIL_EXISTED);
        }
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

    @Transactional
    public void updateMember(PutRequirement requirement){
        var member = findMember();
        member.update(requirement.nickname);
        repository.save(member);
    }

    public record PutRequirement(
        String nickname
    ){}
}
