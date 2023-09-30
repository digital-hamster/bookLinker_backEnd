package com.books_recommend.book_recommend.entity;

import java.util.List;

public class KakaoClient {

//    private Long id;

    private String title;

    private String isbn;

    private List<String> publisher;

    private String thumbnail;

    private String url;

    public KakaoClient(String title,
                       String isbn,
                       List<String> publisher,
                       String thumbnail,
                       String url){
        this.title = title;
        this.isbn = isbn;
        this.publisher = publisher;
        this.thumbnail = thumbnail;
        this.url = url;
    }


}
