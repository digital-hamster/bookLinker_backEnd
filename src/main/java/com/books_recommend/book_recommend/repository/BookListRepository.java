package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.entity.BookList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookListRepository extends JpaRepository<BookList, Long> {
    @Query("select b from BookList b where b.deletedAt is null ORDER BY b.id DESC")
    Page<BookList> findActiveBookList(Pageable pageable);

    @Query("select b from BookList b where b.deletedAt is null ORDER BY b.id DESC")
    BookList findActiveBookList(Long bookListId);
}
