package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.entity.BookList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookListRepository extends JpaRepository<BookList, Long>,
QuerydslPredicateExecutor<BookList>,
BookListRepositoryCustom{

    @Query("select b from BookList b where b.deletedAt is null ORDER BY b.id DESC")
    Page<BookList> findActiveBookList(Pageable pageable);

    @Query("select b from BookList b where b.deletedAt is null ORDER BY b.id DESC")
    BookList findActiveBookList(Long bookListId);

    Page<BookList> searchTitle(SearchCondition searchCondition, Pageable pageable);

    @Query("SELECT b FROM BookList b WHERE b.id = (SELECT c.bookListId FROM Comment c WHERE c.id = :commentId)")
    BookList findBookListByCommentId(Long commentId);

    Optional<BookList> findById(Long bookListId);
}
