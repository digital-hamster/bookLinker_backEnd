package com.books_recommend.book_recommend.entity;

import com.books_recommend.book_recommend.common.entity.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private String image;

    @Column
    private String link;

    @Column(name= "deleted_at")
    private LocalDateTime deletedAt;

//    @JsonIgnore
//    @ManyToOne
//    @JoinColumn(name = "member_id")
//    private Member member;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bookList_id")
    private BookList bookList;

    public Book(BookList bookList,
                String title,
                String content,
                String link,
                String image){
        this.bookList = bookList;
        this.title = title;
        this.content = content;
        this.link = link;
        this.image = image;
    }

    public void remove(){
        this.deletedAt = LocalDateTime.now();
    }

    //booklist 양방향 관계를 설정
    public void addBookList(BookList bookList) {
        this.bookList = bookList;
        if (!bookList.getBooks().contains(this)) {
            bookList.getBooks().add(this);
        }
    }
}
