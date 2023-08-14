package com.books_recommend.book_recommend.dto;

import com.books_recommend.book_recommend.entity.Book;
import com.books_recommend.book_recommend.entity.BookList;


import java.util.List;
import java.util.stream.Collectors;

public record BookListDto (
    List<BookDto> books,
    Long bookListId,
    Long memberId,
    String title,
    String content,
    String hashTag,
    String backImg
){
    //for other
    public static BookListDto fromEntity(BookList list) {
        List<BookDto> bookDtos = list.getBooks().stream()
                .map(BookDto::fromEntity)
                .collect(Collectors.toList());

        return new BookListDto(
                bookDtos,
                list.getId(),
                list.getMember().getId(),
                list.getTitle(),
                list.getContent(),
                list.getHashTag(),
                list.getBackImg()
        );
    }

    //for create
        public static BookListDto fromEntity(BookList savedList, List<Book> books) {
            List<BookDto> bookDtos = books.stream()
                .map(BookDto::fromEntity)
                .collect(Collectors.toList());

        return new BookListDto(
                bookDtos,
                savedList.getId(),
                savedList.getMember().getId(),
                savedList.getTitle(),
                savedList.getContent(),
                savedList.getHashTag(),
                savedList.getBackImg()
        );
    }

}