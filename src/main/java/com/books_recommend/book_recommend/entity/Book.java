package com.books_recommend.book_recommend.entity;

import com.books_recommend.book_recommend.common.entity.BaseTimeEntity;
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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bookList_id")
    private BookList bookList; //bookList

//    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
//    private List<BookListInfo> bookListInfo = new ArrayList<>();


    public Book(String title,
                String content,
                String link,
                String image){
        this.title = title;
        this.content = content;
        this.link = link;
        this.image = image;
    }

    public void remove(){
        this.deletedAt = LocalDateTime.now();
    }

    //외래키 추가
    public void addMember(Member member) {
        this.member = member;  // 현재 Book 엔티티에 Member를 설정
        if (!member.getBooks().contains(this)) {
            member.getBooks().add(this);  // Member 엔티티에도 현재 Book을 추가
        }
    }

}
