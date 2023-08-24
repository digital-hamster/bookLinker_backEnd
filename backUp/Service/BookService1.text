package com.books_recommend.book_recommend.service;

import com.books_recommend.book_recommend.common.exception.BusinessLogicException;
import com.books_recommend.book_recommend.common.exception.ExceptionCode;
import com.books_recommend.book_recommend.dto.BookDto;
import com.books_recommend.book_recommend.entity.Book;
import com.books_recommend.book_recommend.entity.Member;
import com.books_recommend.book_recommend.repository.BookRepository;
import com.books_recommend.book_recommend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public BookDto createBook(BookService.CreateRequirement requirement, Long memberId){
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        Book book = requirement.toEntity();
        book.addMember(member);
        Book saveBook = bookRepository.save(book);

        return BookDto.fromEntity(saveBook);
    }

    @Transactional(readOnly = true)
    public List<BookDto> getBooks(){
        var books = bookRepository
            .findAll();

        return books.stream()
            .map(BookDto::fromEntity)
            .toList();
    }

    public record CreateRequirement(
        String title,
        String content,
        String link,
        String image
    ){
        public Book toEntity(){
            return new Book(title, content, link, image);
        }
    }
}
