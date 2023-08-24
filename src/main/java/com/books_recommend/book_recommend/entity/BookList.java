package com.books_recommend.book_recommend.entity;

import com.books_recommend.book_recommend.common.entity.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookList extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String backImg;

    @Column
    private String content;

    @Column(name = "hash_tag")
    private String hashTag; //content2 느낌
    //해시태그 받을 때 띄어쓰기 안됨 ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ

    @Column
    @OneToMany(mappedBy = "bookList", cascade = CascadeType.REMOVE)
    private List<Book> books = new ArrayList<>();
    //1. 봐바 이건 연관관계임, 담는 관계를 보면 book을 리스트로 저장하겟다는거임

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonIgnore
    @OneToMany(mappedBy = "bookList", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public BookList(
            String title,
            String content,
            String hashTag,
            String backImg
    ){
        this.title = title;
        this.content = content;
        this.hashTag = hashTag;
        this.backImg = backImg;
    }

    public void addMember(Member member) {
        this.member = member;
        if (!member.getBookLists().contains(this)) {
            member.getBookLists().add(this);
        }
    }

    public void addBooks(List<Book> books) {
        this.books.addAll(books);
    }
}

