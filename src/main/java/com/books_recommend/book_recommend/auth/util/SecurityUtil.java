package com.books_recommend.book_recommend.auth.util;

import com.books_recommend.book_recommend.auth.config.JwtTokenizer;
import com.books_recommend.book_recommend.common.exception.BusinessLogicException;
import com.books_recommend.book_recommend.common.exception.ExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    //토큰 인증이 끝난 토큰을 뺏어와서 토큰의 이메일을 뺏어옴
    public static String getMemberEmail() {
        Authentication authentication = roadAuthentication();

        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }

        throw new BusinessLogicException(ExceptionCode.AUTHENTICATION_FAILED_MEMBER);
    }
    public static Authentication roadAuthentication(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessLogicException(ExceptionCode.AUTHENTICATION_FAILED_MEMBER);
        }

        return authentication;
    }
}

