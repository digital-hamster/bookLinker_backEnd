package com.books_recommend.book_recommend.dto;

import com.books_recommend.book_recommend.entity.Book;

import java.util.List;

public record BookDto (
    Long id,
    String title,
    String authors,
    String isbn,
    String publisher,
    String image,
    String url,
    String recommendation
){
    public static BookDto fromEntity(Book book){
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthors(),
                book.getIsbn(),
                book.getPublisher(),
                book.getImage(),
                book.getUrl(),
                book.getRecommendation()
        );
    }
}
