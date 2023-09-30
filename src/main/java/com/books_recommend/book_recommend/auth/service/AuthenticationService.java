package com.books_recommend.book_recommend.auth.service;

import com.books_recommend.book_recommend.auth.config.JwtTokenizer;
import com.books_recommend.book_recommend.auth.dto.AuthenticationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUserDetailsService userDetailsService;
    private final JwtTokenizer jwtTokenizer;

    public AuthenticationDto authenticate(Requirement requirement){
        var member = userDetailsService.authenticateByEmailAndPassword(
            requirement.email,
            requirement.password
        );

        var userDetails = userDetailsService.loadUserByUsername(requirement.email);

        var token = jwtTokenizer.generateTokenForMember(
            member.getId(),
            member.getEmail(),
            userDetails.getAuthorities()
        );

        return new AuthenticationDto(token);
    }


    public record Requirement(
        String email,
        String password
    ){}
}

