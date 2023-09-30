package com.books_recommend.book_recommend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

    @Getter
    @AllArgsConstructor
    public enum ExceptionCode {
        //auth
        TOKEN_EXPIRED("만료된 토큰입니다."),
        TOKEN_NOT_UNABLE("토큰을 얻을 수 없습니다."),
        TOKEN_NOT_BEGIN_BEARER("토큰의 형식이 Bearer로 시작하지 않습니다."),

        AUTHENTICATION_FAILED_MEMBER("인증이 실패한 사용자입니다."), //로그인 세션 만료 경우, 인증에 대한 결과가 없는 경우
        PASSWORD_NOT_CORRECT("비밀번호가 일치하지 않습니다."),

        //List
        LIST_NOT_FOUND("존재하지 않는 리스트 입니다."),
        MEMBER_NOT_DELETE_LIST("리스트를 삭제할 수 없는 사용자 입니다."),

        //Member
        MEMBER_NOT_FOUND("존재하지 않는 사용자 입니다"),
        MEMBER_NOT_WRITER("작성자 본인이 아닙니다."),

        //Comment
        COMMENT_NOT_FOUND("존재하지 않는 댓글 입니다."),

        BOOK_NOT_FOUND("존재하지 않는 책입니다.");


        private String message;
    }

