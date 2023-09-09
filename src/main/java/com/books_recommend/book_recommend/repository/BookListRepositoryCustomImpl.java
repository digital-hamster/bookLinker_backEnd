package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.entity.BookList;
import com.books_recommend.book_recommend.entity.QBookList;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookListRepositoryCustomImpl implements BookListRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public BookListRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<BookList> searchTitle(SearchCondition searchCondition, Pageable pageable) {
        QBookList bookList = QBookList.bookList;

        var searchTerm = searchCondition.title().orElse(""); // 검색어
        var searchWord = searchTerm.toLowerCase().replaceAll("\\s+", ""); // 대소문자 무시, 띄어쓰기 제거

        List<Predicate> predicates = new ArrayList<>(); //동적으로 조건을 추가하기 위해 배열 생성

        if (!searchWord.isEmpty()) {
            predicates.add(Expressions.stringTemplate("LOWER(REPLACE({0}, ' ', ''))", bookList.title)
                .like("%" + searchWord + "%")); //> 일부분 일치 적용
        }
        //Expressions.stringTemplate("LOWER(REPLACE({0}, ' ', ''))"
        //ㄴ> LOWER: 문자열을 소문자로 변환
        //ㄴ> REPLACE({0}, ' ', ''): 문자열 모든 공백 문자를 제거

        // deletedAt이 null인걸 추가 > 이 조건을 추가하기 위해서 predicates를 arrayList로 추가함
        predicates.add(bookList.deletedAt.isNull());

        var where = predicates.toArray(new Predicate[0]);

        // 쿼리 계산 카운트 먼저 호출하기 (빈 페이지일 경우 바로 반환해서 2번 추가할 거 1번에 끝냄)
        Long countQuery = queryFactory
            .select(Wildcard.count)
            .from(bookList)
            .where(where)
            .fetchOne();

        if (countQuery == null || countQuery == 0) {
            // 검색 결과가 없을 때 빈 페이지를 반환
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        var query = queryFactory
            .selectFrom(bookList)
            .where(where)
            .orderBy(bookList.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(query, pageable, countQuery);
    }
}