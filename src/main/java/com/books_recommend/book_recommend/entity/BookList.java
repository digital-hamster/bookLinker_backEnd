package com.books_recommend.book_recommend.entity;

import com.books_recommend.book_recommend.common.entity.BaseTimeEntity;
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
    private Integer count;

    @Column
    private LocalDateTime deletedAt;

    private Long memberId;

    @Column
    @OneToMany(mappedBy = "bookList", cascade = CascadeType.ALL)
    private List<Book> books = new ArrayList<>();

    public BookList(
            String title,
            String content,
            String hashTag,
            String backImg,
            Long memberId
    ){
        this.title = title;
        this.content = content;
        this.hashTag = hashTag;
        this.backImg = backImg;
        this.memberId = memberId;
        this.count = 0;
    }

    public void addBooks(List<Book> books) {
        for(Book book : books){
            this.books.add(book);
        }
    }

    public void remove(){
        this.deletedAt = LocalDateTime.now();
    }

    public void incrementCount(){
        this.count++;
    }

    public void setImg(String backImg){
        this.backImg = backImg;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setHashTag(String hashTag){
        this.hashTag = hashTag;
    }
}

