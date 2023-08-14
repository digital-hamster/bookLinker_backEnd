package com.books_recommend.book_recommend.service;

import com.books_recommend.book_recommend.common.exception.BusinessLogicException;
import com.books_recommend.book_recommend.common.exception.ExceptionCode;
import com.books_recommend.book_recommend.dto.BookListDto;
import com.books_recommend.book_recommend.entity.Book;
import com.books_recommend.book_recommend.entity.BookList;
import com.books_recommend.book_recommend.entity.Member;
import com.books_recommend.book_recommend.repository.BookListRepository;
import com.books_recommend.book_recommend.repository.BookRepository;
import com.books_recommend.book_recommend.repository.MemberRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookListService {
    private final BookRepository bookRepository;
    private final BookListRepository bookListrepository;
    private final MemberRepository memberRepository;

    //create
    @Transactional
    public BookListDto create(CreateRequirement requirement, Long memberId){
        Member member = memberRepository.findById(memberId)
            .orElseThrow(()-> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

//        var books = bookRepository.findAllById(requirement.books());
        //ㄴ> 이제 id를 받아올 필요가 없

        var list = requirement.toEntity();

        List<Book> books = BooksRequirement.createBooks(requirement, list);

        list.addMember(member);
        addBooks(list, books);
//        for (Book book : books) {
//            book.addBookList(list);
//        }

        var savedList = bookListrepository.save(list);
        return BookListDto.fromEntity(savedList, books);
    }

    public void addBooks(BookList bookList, List<Book> books) {
        for ( Book book : books) {
            bookList.getBooks().add(book);
        }
    }

    public record CreateRequirement(
        String title,
        String content,
        String hashTag,
        String backImg,
        List<BooksRequirement> books
    ){
        public BookList toEntity(){
            return new BookList(
                title,
                content,
                hashTag,
                backImg
            );
        }
    }

    public record BooksRequirement(
        String title,
        String content,
        String link,
        String image
    ){
        public Book toEntity(BookList bookList){
            return new Book(
                bookList,
                title,
                content,
                link,
                image
            );
        }

        public static List<Book> createBooks(CreateRequirement requirement, BookList bookList) {
            return requirement.books().stream()
                .map(bookRequirement -> bookRequirement.toEntity(bookList))
                .toList();
        }
    }

    //get
    @Transactional(readOnly = true)
    public BookListDto getList(Long listId){
        var list = bookListrepository
            .findById(listId)
            .orElseThrow(()-> new BusinessLogicException(ExceptionCode.LIST_NOT_FOUND));

        return BookListDto.fromEntity(list);
    }
}
