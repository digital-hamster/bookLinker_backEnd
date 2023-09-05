package com.books_recommend.book_recommend.dto;

import jakarta.persistence.Column;

import java.util.List;

public record KakaoBookDto (
    String title,

    List<String> authors,

    String isbn,

    String publisher,

    String image, //thumbnail

    String url
){}
