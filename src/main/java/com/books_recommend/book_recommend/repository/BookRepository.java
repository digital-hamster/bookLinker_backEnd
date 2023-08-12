package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
