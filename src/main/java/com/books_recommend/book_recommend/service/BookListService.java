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

        var books = bookRepository.findAllById(requirement.bookIds()); //이건 책의 정보(book)이지 bookList가 아님

        //requirement를 entity로 바꿔서 books를 담자
        var list = requirement.toEntity();

        list.addMember(member);
        for (Book book : books) {
            book.addBookList(list); // 각 Book 객체의 bookList 연관관계 설정
        }

        var savedList = bookListrepository.save(list);
        return BookListDto.fromEntity(savedList, books);
    }

    public record CreateRequirement(
        List<Long> bookIds,
        String title,
        String backImg,
        String content
    ){
        public BookList toEntity(){
            return new BookList(
                title,
                backImg,
                content
            );
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
