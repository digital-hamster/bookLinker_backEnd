package com.books_recommend.book_recommend.auth.handle;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // 사용자 인증 실패 에러 핸들러
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws IOException, ServletException {

        String errorMessage = e.getMessage(); // 예외 메시지 가져오기

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 상태코드 설정
        response.getWriter().write(errorMessage); // 예외 메시지 전송
    }
}