package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.bookListId = :bookListId ORDER BY c.createdAt DESC")
    List<Comment> findAllByBookListId(Long bookListId);

    @Query("SELECT c.bookListId FROM Comment c GROUP BY c.bookListId ORDER BY COUNT(c.bookListId) DESC")
    List<Long> findBookListIdsByCommentDesc();

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.bookListId = :bookListId")
    Long countCommentByBookListId(Long bookListId);
}
