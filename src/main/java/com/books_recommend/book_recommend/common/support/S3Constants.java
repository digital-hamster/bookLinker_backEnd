package com.books_recommend.book_recommend.common.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum S3Constants {

    FILE_DiRECTORY("background/"), //image.png"

    BUCKET_NAME("book-linker-bucket");

    final String SeriesConstant;

}
