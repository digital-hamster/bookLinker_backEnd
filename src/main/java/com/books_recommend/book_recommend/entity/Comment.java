package com.books_recommend.book_recommend.entity;

import com.books_recommend.book_recommend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    private Long bookListId;

    private Long memberId;

    public Comment(String content,
                   Long bookListId,
                   Long memberId) {
        this.content = content;
        this.bookListId = bookListId;
        this.memberId = memberId;
    }

    public void update(String content){
        this.content = content;
    }
}
