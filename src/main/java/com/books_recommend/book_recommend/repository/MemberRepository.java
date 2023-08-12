package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
