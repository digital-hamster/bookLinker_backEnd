package com.books_recommend.book_recommend.service;

import com.books_recommend.book_recommend.auth.util.SecurityUtil;
import com.books_recommend.book_recommend.common.exception.BusinessLogicException;
import com.books_recommend.book_recommend.common.exception.ExceptionCode;
import com.books_recommend.book_recommend.dto.BookDto;
import com.books_recommend.book_recommend.dto.BookListDto;
import com.books_recommend.book_recommend.entity.Book;
import com.books_recommend.book_recommend.entity.BookList;
import com.books_recommend.book_recommend.entity.Member;
import com.books_recommend.book_recommend.repository.BookListRepository;
import com.books_recommend.book_recommend.repository.BookListRepositoryCustom;
import com.books_recommend.book_recommend.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//3. 특정 BookList 검색하기 (title)

@Service
@RequiredArgsConstructor
public class BookListService {
    private final BookListRepository bookListRepository;
    private final BookRepository bookRepository;
    private final MemberService memberService;

    @Transactional
    public Long create(CreateRequirement requirement) {
        var member = memberService.findMember();
        var bookList = createBookList(requirement, member);
        var books = createBooks(requirement, bookList);

        bookList.addBooks(books);

        var savedBookList = bookListRepository.save(bookList);
        addMapping(books, bookList);
        bookRepository.saveAll(books); //자동적으로 books 저장이 안됨

        return savedBookList.getId();
    }

    private void addMapping(List<Book> books,
                            BookList bookList
    ) {
        books.forEach(book -> {
            book.addBookList(bookList);
//            book.addMember(member);
        });
    }

    private static List<Book> createBooks(CreateRequirement requirement,
                                          BookList bookList) {
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

    private static BookList createBookList(CreateRequirement requirement, Member member) {
        return new BookList(
            requirement.title,
            requirement.content,
            requirement.hashTag,
            requirement.backImg,
            member.getId()
        );
    }

    public record CreateRequirement(
        String title,
        String content,
        String hashTag,
        String backImg,
        List<BookRequirement> booksRequest
    ) {
        public record BookRequirement(
            String title,
            String authors,
            String isbn,
            String publisher,
            String image,
            String url,
            String recommendation
        ) {
        }
    }

    @Transactional(readOnly = true)
    public Page<BookListDto> findSearchLists(BookListService.SearchRequirement requirement, Pageable pageable) {
        var searchCondition = new BookListRepositoryCustom.SearchCondition(requirement.title);
        var lists = bookListRepository.searchTitle(searchCondition, pageable);

        return dtosWithWriter(lists);
    }

    public record SearchRequirement(
        Optional<String> title
    ){}

    @Transactional(readOnly = true)
    public Page<BookListDto> findAllLists(Pageable pageable) {
        var lists = bookListRepository.findActiveBookList(pageable);

        return dtosWithWriter(lists);
    }

    private Page<BookListDto> dtosWithWriter(Page<BookList> lists) {
        var dtosWithWriter = lists.stream()
            .map(list -> {
                var isWriter = isWriter(list, memberService);
                var bookDtos = list.getBooks().stream()
                    .map(this::toBookDto) // BookDto로 변환
                    .collect(Collectors.toList());
                return new BookListDto(
                    bookDtos,
                    list.getId(),
                    isWriter,
                    list.getMemberId(),
                    list.getTitle(),
                    list.getContent(),
                    list.getHashTag(),
                    list.getBackImg(),
                    list.getCount()
                );
            })
            .toList();

        return new PageImpl<>(
            dtosWithWriter,
            lists.getPageable(),
            lists.getTotalElements()
        );
    }

    private BookDto toBookDto(Book book) {
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

    @Transactional //(readOnly = true) 조회수가 들어가기 때문에 읽기 전용은 뺀다
    public BookListDto getBookList(Long bookListId) {
        BookList list = findBookListById(bookListId);
        valifyList(list);
        list.incrementCount();
        bookListRepository.save(list);

        var isWriter = isWriter(list, memberService);
        var books = fromEntity(list);

        return new BookListDto(
            books,
            list.getId(),
            isWriter,
            list.getMemberId(),
            list.getTitle(),
            list.getContent(),
            list.getHashTag(),
            list.getBackImg(),
            list.getCount());
    }

    private static Boolean isWriter(BookList list, MemberService memberService) {
        if (SecurityUtil.hasToken() &&
            memberService.findMember().getId() == list.getMemberId()){
                return true;
            }
        return false;
    }

    private List<BookDto> fromEntity(BookList list) {
        return list.getBooks().stream()
            .map(BookDto::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public Long remove(Long bookListId) {
        var member = memberService.findMember();

        BookList list = findBookListById(bookListId);

        valifyList(list);

        if(!Objects.equals(member.getId(), list.getMemberId())){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_WRITER);
        }

        list.remove();
        return list.getId();
    }

    @Transactional
    public Long update(UpdateRequirement requirement,
                       Long bookListId) {
        var member = memberService.findMember();

        BookList list = findBookListById(bookListId);

        valifyList(list);

        if(list.getMemberId() != member.getId()){
            throw new BusinessLogicException(ExceptionCode.IS_NOT_WRITER);
        }

        var updateContent = fromRequirement(requirement, member);

        var books = bookRepository.findByBookListId(bookListId);
        var updateBooksContent = fromRequirement(requirement, list);


        if(!Objects.equals(member.getId(), list.getMemberId())){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_WRITER);
        }

        list.update(updateContent);
        //books update
        IntStream.range(0, books.size())
            .forEach(i -> books.get(i).update(updateBooksContent.get(i)));

        bookListRepository.save(list);
        bookRepository.saveAll(books);

        return list.getId();
    }

    private static BookList fromRequirement(UpdateRequirement requirement, Member member) {
        return new BookList(
            requirement.title,
            requirement.content,
            requirement.hashTag,
            requirement.backImg,
            member.getId()
        );
    }

    private static List<Book> fromRequirement(UpdateRequirement requirement,
                                          BookList bookList) {
        return requirement.books().stream()
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

    public record UpdateRequirement(
        String title,
        String content,
        String hashTag,
        String backImg,
        List<UpdateRequirement.BookRequirement> books
    ) {

        public record BookRequirement(
            String title,
            String authors,
            String isbn,
            String publisher,
            String image,
            String url,
            String recommendation
        ) {}
    }

    public static void valifyList(BookList list){
        if(list.getDeletedAt()!= null){
            throw new BusinessLogicException(ExceptionCode.LIST_ALREADY_DELETED);
        }
    }

    public BookList findBookListById(Long bookListId) {
        var list = bookListRepository.findById(bookListId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.LIST_NOT_FOUND));
        return list;
    }
}
