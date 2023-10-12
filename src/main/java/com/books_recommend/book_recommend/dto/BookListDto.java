package com.books_recommend.book_recommend.dto;

import java.util.List;

public record BookListDto(
    List<BookDto> books,

    Long bookListId,

    Boolean isWriter,

    Long memberId,

    String title,

    String content,

    String hashTag,

    String backImg,

    Integer count
) {
    public BookListDto(
        List<BookDto> books,
        Long bookListId,
        Long memberId,
        String title,
        String content,
        String hashTag,
        String backImg,
        Integer count
    ) {
        this(books, bookListId, false, memberId, title, content, hashTag, backImg, count);
    }
}
