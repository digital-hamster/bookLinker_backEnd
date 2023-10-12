package com.books_recommend.book_recommend.dto;

public record ListFavoriteDto(
    Long id,
    Long memberId,
    Long bookListId
){
    public record GetListFavoriteDto(
        Long id,
        Long memberId,

        Boolean isFavorite,
        Long bookListId
    ){}
}
