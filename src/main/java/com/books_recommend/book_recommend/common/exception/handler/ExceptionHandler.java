package com.books_recommend.book_recommend.common.exception.handler;

import com.books_recommend.book_recommend.common.exception.BusinessLogicException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<String> handleException(BusinessLogicException ex){

        ResponseEntity<String> response = ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getExceptionCode().getMessage());

        return response;
    }
}
