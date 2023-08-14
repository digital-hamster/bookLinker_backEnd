package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.entity.BookList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookListRepository extends JpaRepository<BookList, Long> {
}
