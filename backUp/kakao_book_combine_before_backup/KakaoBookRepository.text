package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.entity.KakaoBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoBookRepository extends JpaRepository<KakaoBook, Long> {
    KakaoBook findByIsbn(String isbn);
}
