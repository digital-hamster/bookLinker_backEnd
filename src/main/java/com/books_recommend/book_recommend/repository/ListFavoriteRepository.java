package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.entity.ListFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ListFavoriteRepository extends JpaRepository<ListFavorite, Long> {
    @Query("SELECT f FROM ListFavorite f WHERE f.bookListId = :bookListId ORDER BY f.createdAt DESC")
    List<ListFavorite> findByBookListId(Long bookListId);

    @Query("SELECT f FROM ListFavorite f WHERE f.memberId = :memberId ORDER BY f.createdAt DESC")
    List<ListFavorite> findByMemberId(Long memberId);

    @Query("SELECT COUNT(f) FROM ListFavorite f WHERE f.memberId = :memberId AND f.bookListId = :bookListId")
    Long countByMemberIdAndBookListId(Long memberId, Long bookListId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN TRUE ELSE FALSE END FROM ListFavorite f WHERE f.bookListId = :bookListId")
    boolean existsByBookListId(Long bookListId);

    //좋아요 순이 제일 많은 bookListId 순서로 반환
    @Query("SELECT f.bookListId FROM ListFavorite f GROUP BY f.bookListId ORDER BY COUNT(f.bookListId) DESC")
    List<Long> findBookListIdsByFavoriteDesc();

    //좋아요 개수
    @Query("SELECT COUNT(f) FROM ListFavorite f WHERE f.bookListId = :bookListId")
    Long countFavoriteByBookListId(Long bookListId);
}
