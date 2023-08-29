package com.books_recommend.book_recommend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String title;

    @Column(length = 500)
    private String authors;

    @Column(length = 500)
    private String isbn;

    @Column(length = 500)
    private String publisher;

    @Column(length = 500)
    private String thumbnail; //image

    @Column(length = 500)
    private String url;

    public KakaoBook(
        String title,
        String authors,
        String isbn,
        String publisher,
        String thumbnail,
        String url
    ){
        this.title = title;
        this.authors = authors;
        this.isbn = isbn;
        this.publisher = publisher;
        this.thumbnail = thumbnail;
        this.url = url;
    }

    public void updateFrom(KakaoBook newBook) { //수정하지 않고 저장하면, id와 url만 저장되는 사태 발생
        this.title = newBook.getTitle();
        this.authors = newBook.getAuthors();
        this.publisher = newBook.getPublisher();
        this.thumbnail = newBook.getThumbnail();
        this.url = newBook.getUrl();
    }
}
