package com.books_recommend.book_recommend.service;

import com.books_recommend.book_recommend.common.exception.BusinessLogicException;
import com.books_recommend.book_recommend.common.exception.ExceptionCode;
import com.books_recommend.book_recommend.dto.BookDto;
import com.books_recommend.book_recommend.dto.BookListDto;
import com.books_recommend.book_recommend.entity.Book;
import com.books_recommend.book_recommend.entity.BookList;
import com.books_recommend.book_recommend.entity.Member;
import com.books_recommend.book_recommend.repository.BookListRepository;
import com.books_recommend.book_recommend.repository.BookRepository;
import com.books_recommend.book_recommend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookListService {
    private final BookListRepository bookListRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    @Transactional
    public Long create(CreateRequirement requirement, Long memberId){
        var member = memberRepository.findById(memberId)
            .orElseThrow(()-> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        var bookList = createBookList(requirement);
        var books = createBooks(requirement, bookList);

        bookList.addBooks(books);
        bookList.addMember(member);

        var savedBookList = bookListRepository.save(bookList);
        addMapping(books, bookList, member);
        bookRepository.saveAll(books); //자동적으로 books 저장이 안됨

        return savedBookList.getId();
    }

    private void addMapping(List<Book> books, BookList bookList, Member member) {
        books.forEach(book -> {
            book.addBookList(bookList);
            book.addMember(member);
        });
    }

    private static List<Book> createBooks(CreateRequirement requirement, BookList bookList){
        return requirement.booksRequest.stream()
            .map(bookRequirement -> new Book(
                bookList,
                bookRequirement.title,
                bookRequirement.authors,
                bookRequirement.isbn,
                bookRequirement.publisher,
                bookRequirement.image,
                bookRequirement.url,
                bookRequirement.recommendation
            ))
            .toList();
    }

    private static BookList createBookList(CreateRequirement requirement){
        return new BookList(
            requirement.title,
            requirement.content,
            requirement.hashTag,
            requirement.backImg
        );
    }

    public record CreateRequirement(
        String title,
        String content,
        String hashTag,
        String backImg,
        List<BookRequirement> booksRequest
    ){
        public record BookRequirement(
            String title,
            String authors,
            String isbn,
            String publisher,
            String image,
            String url,
            String recommendation
        ){}
    }

    @Transactional(readOnly = true)
    public List<BookListDto> findAllLists() {
        List<BookList> lists = bookListRepository.findAll();

        return lists.stream()
            .map(BookListService::toListDto)
            .collect(Collectors.toList());
    }

    private static BookListDto toListDto(BookList list) {
        List<BookDto> bookDtos = list.getBooks().stream()
            .map(BookListService::toBookDto)
            .collect(Collectors.toList());

        return new BookListDto(
            bookDtos,
            list.getId(),
            list.getMember().getId(),
            list.getTitle(),
            list.getContent(),
            list.getHashTag(),
            list.getBackImg()
        );
    }

    private static BookDto toBookDto(Book book) {
        return new BookDto(
            book.getId(),
            book.getTitle(),
            book.getAuthors(),
            book.getIsbn(),
            book.getPublisher(),
            book.getImage(),
            book.getUrl(),
            book.getRecommendation()
        );
    }
}
