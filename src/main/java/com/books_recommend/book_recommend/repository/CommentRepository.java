package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
