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

    @Column
    private String title;

    @Column
    private String authors;

    @Column
    private String isbn;

    @Column
    private String publisher;

    @Column
    private String thumbnail; //image

    @Column
    private String url;

    protected KakaoBook(
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
}
