package com.books_recommend.book_recommend.entity;

import com.books_recommend.book_recommend.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

//@Entity(name = "list")
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

    @Column
    @OneToMany(mappedBy = "bookList", cascade = CascadeType.REMOVE)
    private List<Book> books = new ArrayList<>();
    //1. 봐바 이건 연관관계임, 담는 관계를 보면 book을 리스트로 저장하겟다는거임

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public BookList(
            String title,
            String content,
            String backImg
    ){
        this.title = title;
        this.content = content;
        this.backImg = backImg;
    }

    public void addMember(Member member) {
        this.member = member;
        if (!member.getBookLists().contains(this)) {
            member.getBookLists().add(this);
        }
    }

//    public void addBook(Book book) {
//        books.add(book);
//        book.addBookList(this);
//    }
//
//    public void addBooks(List<Book> books) {
//        this.books.addAll(books);
//        for (Book book : books) {
//            if (!book.getBookList().equals(this)) {
//                book.addBookList(this);
//            }
//        }

//    public void addBook(Book book){
//        this.book
//    }
    }

