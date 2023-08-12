package com.books_recommend.book_recommend.dto;

import com.books_recommend.book_recommend.entity.Book;

public record BookDto (
    Long id,
    Long memberId,
    String title,
    String content,
    String link,
    String image
){
    public static BookDto fromEntity(Book book){
        return new BookDto(
                book.getId(),
                book.getMember().getId(),
                book.getTitle(),
                book.getContent(),
                book.getLink(),
                book.getImage()
        );
    }
}
