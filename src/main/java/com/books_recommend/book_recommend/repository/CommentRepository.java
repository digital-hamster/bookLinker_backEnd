package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.bookList.id = :bookListId ORDER BY c.createdAt DESC")
    List<Comment> findByBookListId(Long bookListId);
}
