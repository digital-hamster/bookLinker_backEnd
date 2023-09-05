package com.books_recommend.book_recommend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

    @Getter
    @AllArgsConstructor
    public enum ExceptionCode {
        LIST_NOT_FOUND("존재하지 않는 리스트 입니다."),
        MEMBER_NOT_FOUND("존재하지 않는 유저입니다"),
        BOOK_NOT_FOUND("존재하지 않는 책입니다.");

        private String message;
    }

