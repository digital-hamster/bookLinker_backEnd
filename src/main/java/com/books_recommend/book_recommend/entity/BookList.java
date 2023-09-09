package com.books_recommend.book_recommend.entity;

import com.books_recommend.book_recommend.common.entity.BaseTimeEntity;
import com.books_recommend.book_recommend.service.BookListService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    @Column
    @OneToMany(mappedBy = "bookList", cascade = CascadeType.REMOVE)
    private List<Book> books = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonIgnore
    @OneToMany(mappedBy = "bookList", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Column
    private LocalDateTime deletedAt;

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
        for(Book book : books){
            this.books.add(book);
        }
    }

    public void remove(){
        this.deletedAt = LocalDateTime.now();
    }

    public void update(BookList list){
//        this.books = list.getBooks();
        this.title = list.title;
        this.content = list.content;
        this.hashTag = list.hashTag;
        this.backImg = list.backImg;
    }
}

