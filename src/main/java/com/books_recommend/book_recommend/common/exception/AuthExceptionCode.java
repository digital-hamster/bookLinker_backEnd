package com.books_recommend.book_recommend.common.exception;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public enum AuthExceptionCode {

    //auth
    TOKEN_EXPIRED("만료된 토큰입니다."),
    TOKEN_NOT_UNABLE("토큰을 얻을 수 없습니다."),
    TOKEN_NOT_BEGIN_BEARER("토큰의 형식이 Bearer로 시작하지 않습니다."),
    TOKEN_NOT_FOUND("토큰이 없습니다."),
    EXPIRED_TOKEN("토큰이 만료되었습니다."),
    OTHER_TOKEN("다른 토큰 예외가 발생했습니다.");

    private final String message;

    AuthExceptionCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void handleException(HttpServletResponse response) throws IOException {
        setUnauthorizedStatus(response);
        response.getWriter().write(getMessage());
    }

    public static void handleException(HttpServletResponse response, AuthExceptionCode exceptionType) throws IOException {
        exceptionType.handleException(response);
    }

    public static void setUnauthorizedStatus(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}