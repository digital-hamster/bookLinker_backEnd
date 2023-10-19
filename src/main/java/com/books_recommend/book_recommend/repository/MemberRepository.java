package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT m.nickName FROM Member m WHERE m.id = :memberId")
    String findNicknameById(Long memberId);
}
