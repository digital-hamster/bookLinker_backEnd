package com.books_recommend.book_recommend.entity;

import com.books_recommend.book_recommend.common.entity.BaseTimeEntity;
import com.books_recommend.book_recommend.service.BookListService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String title;

    @Column(length = 50)
    private String authors;

    @Column(length = 50)
    private String isbn;

    @Column(length = 50)
    private String publisher;

    @Column(length = 500)
    private String image; //thumbnail

    @Column(length = 500)
    private String url;

    @Column(length = 500)
    private String recommendation; //사용자 추천사

    @Column(name= "deleted_at")
    private LocalDateTime deletedAt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "book_list_id")
    private BookList bookList;

    public Book(BookList bookList,
                String title,
                String authors,
                String isbn,
                String publisher,
                String image,
                String url,
                String recommendation){
        this.title = title;
        this.authors = authors;
        this.isbn = isbn;
        this.publisher = publisher;
        this.image = image;
        this.url = url;
        this.recommendation = recommendation;
    }

    public void remove(){
        this.deletedAt = LocalDateTime.now();
    }

    //booklist 양방향 관계를 설정
    public void addMember(Member member) {
        this.member = member;
        if (!member.getBooks().contains(this)) {
            member.getBooks().add(this);
        }
    }

    public void addBookList(BookList bookList) {
        this.bookList = bookList;
        if (!bookList.getBooks().contains(this)) {
            bookList.getBooks().add(this);
        }
    }

    public void update(Book book){
        this.title = book.title;
        this.authors = book.authors;
        this.isbn = book.isbn;
        this.publisher = book.publisher;
        this.image = book.image;
        this.url = book.url;
        this.recommendation = book.recommendation;
    }
}
