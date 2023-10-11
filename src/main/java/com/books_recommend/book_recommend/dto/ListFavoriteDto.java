package com.books_recommend.book_recommend.dto;

public record ListFavoriteDto(
    Long id,
    Long memberId,
    Long bookListId
){}
