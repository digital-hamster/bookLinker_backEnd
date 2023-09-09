package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT b FROM Book b WHERE b.bookList.id = :bookListId")
    List<Book> findByBookListId(Long bookListId);
}
