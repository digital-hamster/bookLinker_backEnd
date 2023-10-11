package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.entity.ListFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ListFavoriteRepository extends JpaRepository<ListFavorite, Long> {
    @Query("SELECT lf FROM ListFavorite lf WHERE lf.bookListId = :bookListId")
    Optional<ListFavorite> findFavoriteByBookListId(Long bookListId);

    @Query("SELECT lf FROM ListFavorite lf WHERE lf.bookListId = :bookListId")
    List<ListFavorite> findByBookListId(Long bookListId);

    @Query("SELECT lf FROM ListFavorite lf WHERE lf.memberId = :memberId")
    List<ListFavorite> findByMemberId(Long memberId);
}
