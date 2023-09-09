package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.entity.BookList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookListRepositoryCustom {


    Page<BookList> searchTitle(SearchCondition searchCondition, Pageable pageable);

    public record SearchCondition(
        Optional<String> title
    ){}
}
