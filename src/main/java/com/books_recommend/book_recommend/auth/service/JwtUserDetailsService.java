package com.books_recommend.book_recommend.auth.service;

import com.books_recommend.book_recommend.common.exception.BusinessLogicException;
import com.books_recommend.book_recommend.common.exception.ExceptionCode;
import com.books_recommend.book_recommend.entity.Member;

import com.books_recommend.book_recommend.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private MemberRepository memberRepository;


    @Autowired
    public JwtUserDetailsService(MemberRepository memberRepository,
                                 PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override //사용자 정보를 검색하고 반환하는 메서드, 사용자의 인증 요청마다 호출
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        var memberRole = Member.ROLES.MEMBER.toString();
        var adminRole = Member.ROLES.ADMIN.toString();

        //1. 이메일을 기준으로 사용자 정보를 찾음
        var member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        //2. Member 클래스의 ROLES 필드로 사용자의 인증 정보와 권한 정보를 설정
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + memberRole)); //"ROLE_" 접두사를 사용하여 권한 인식

        if (email.equals("waterlove1439@naver.com")) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + adminRole));
        }

        return new User(member.getEmail(), member.getPassword(), grantedAuthorities);
    }

    //사용자의 이메일과 비밀번호를 받아서 실제 인증을 수행 / login / 인증 성공시 토큰 발행
    public Member authenticateByEmailAndPassword(String email,
                                                 String password) {
        var member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        //입력된 비밀번호와 데이터베이스에 저장된 비밀번호를 비교
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BusinessLogicException(ExceptionCode.PASSWORD_NOT_CORRECT);
        }

        return member;
    }
}