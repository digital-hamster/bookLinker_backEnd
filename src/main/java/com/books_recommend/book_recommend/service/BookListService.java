package com.books_recommend.book_recommend.service;

import com.books_recommend.book_recommend.auth.util.SecurityUtil;
import com.books_recommend.book_recommend.common.exception.BusinessLogicException;
import com.books_recommend.book_recommend.common.exception.ExceptionCode;
import com.books_recommend.book_recommend.common.support.S3Constants;
import com.books_recommend.book_recommend.common.util.S3Uploader;
import com.books_recommend.book_recommend.dto.BookDto;
import com.books_recommend.book_recommend.dto.BookListDto;
import com.books_recommend.book_recommend.entity.Book;
import com.books_recommend.book_recommend.entity.BookList;
import com.books_recommend.book_recommend.entity.Member;
import com.books_recommend.book_recommend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.books_recommend.book_recommend.common.exception.ExceptionCode.IMAGE_NULL;

//3. 특정 BookList 검색하기 (title)

@Service
@RequiredArgsConstructor
public class BookListService {
    private final BookListRepository bookListRepository;
    private final BookRepository bookRepository;
    private final MemberService memberService;
    private final ListFavoriteRepository listFavoriteRepository;
    private final CommentRepository commentRepository;

    private final S3Uploader s3Uploader;

    @Transactional
    public Long create(CreateRequirement requirement) {
        var member = memberService.findMember();
        var imgUrl = upload(requirement.backImg);
        var bookList = createBookList(requirement, member, imgUrl);
        var books = createBooks(requirement, bookList);

        bookList.addBooks(books);

        var savedBookList = bookListRepository.save(bookList);
        addMapping(books, bookList);
        bookRepository.saveAll(books); //자동적으로 books 저장이 안됨

        return savedBookList.getId();
    }

    private String upload(MultipartFile backImg) {
        try {
            return s3Uploader.upload(backImg, "background");
        }
        catch (IOException e) {
            throw new BusinessLogicException(IMAGE_NULL);
        }
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

    private static BookList createBookList(CreateRequirement requirement,
                                           Member member,
                                           String backImg) {
        return new BookList(
            requirement.title,
            requirement.content,
            requirement.hashTag,
            backImg,
            member.getId()
        );
    }

    public record CreateRequirement(
        String title,
        String content,
        String hashTag,
        MultipartFile backImg,
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
                var favorite = countFavoriteByBookListId(list.getId());
                var comments = countCommentByBookListId(list.getId());
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
                    list.getCount(),
                    favorite,
                    comments
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
        var favorite = countFavoriteByBookListId(list.getId());
        var comments = countCommentByBookListId(list.getId());
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
            list.getCount(),
            favorite,
            comments
            );
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

        var list = findBookListById(bookListId);

        valifyList(list);

        if(list.getMemberId() != member.getId()){
            throw new BusinessLogicException(ExceptionCode.IS_NOT_WRITER);
        }

        if(!Objects.equals(member.getId(), list.getMemberId())){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_WRITER);
        }

        var updateImg = change(requirement.backImg);
//        var updateContent = fromRequirement(requirement, member, updateImg);

        var books = bookRepository.findByBookListId(bookListId);
        var updateBooksContent = fromRequirement(requirement, list);

        update(list, requirement, updateImg);

        //books update
        IntStream.range(0, books.size())
            .forEach(i -> books.get(i).update(updateBooksContent.get(i)));

        bookListRepository.save(list);
        bookRepository.saveAll(books);

        return list.getId();
    }

    private void update(BookList bookList, UpdateRequirement requirement, String backImg){
        bookList.setTitle(requirement.title);
        bookList.setContent(requirement.content);
        bookList.setHashTag(requirement.hashTag);
        bookList.setImg(backImg);
    }

    private String change(MultipartFile backImg) {
        try {
            Optional<File> convertedFile = s3Uploader.convert(backImg);

            if (convertedFile.isPresent()) {
                String fileName = S3Constants.FILE_DiRECTORY.getSeriesConstant() + "/" + convertedFile.get().getName();
                return s3Uploader.putS3(convertedFile.get(), fileName);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    private static BookList fromRequirement(UpdateRequirement requirement, Member member, String backImg) {
        return new BookList(
            requirement.title,
            requirement.content,
            requirement.hashTag,
            backImg,
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
        MultipartFile backImg,
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


    //추천 알고리즘=============================================
    public List<BookListDto> getByCount(int offset, int size){
        var lists = bookListRepository.findAllOrderByCountDesc();
        var selectedLists = selectDataByOffsetAndSize(lists, offset, size);

        var dtos = dtosWithRecommend(selectedLists);
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<BookListDto> getByFavorite(int offset, int size){
        var bookListIds = listFavoriteRepository.findBookListIdsByFavoriteDesc();
        var lists = getListByIds(bookListIds);
        var selectedLists = selectDataByOffsetAndSize(lists, offset, size);

        var dtos = dtosWithRecommend(selectedLists);
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<BookListDto> getByComment(int offset, int size){
        var bookListIds = commentRepository.findBookListIdsByCommentDesc();
        var lists = getListByIds(bookListIds);
        var selectedLists = selectDataByOffsetAndSize(lists, offset, size);

        var dtos = dtosWithRecommend(selectedLists);
        return dtos;
    }


    private Long countCommentByBookListId(Long bookListId){
        return commentRepository.countCommentByBookListId(bookListId);
    }

    private Long countFavoriteByBookListId(Long bookListId){
        return listFavoriteRepository.countFavoriteByBookListId(bookListId);
    }

    private List<BookList> getListByIds(List<Long> bookListIds) {
        List<BookList> result = new ArrayList<>();
        for (Long bookListId : bookListIds) {
            List<BookList> bookLists = bookListRepository.findActiveBookLists(bookListId);
            result.addAll(bookLists);
        }
        return result;
    }

    private List<BookList> selectDataByOffsetAndSize(List<BookList> lists, int offset, int size) {
        var totalSize = lists.size();
        if (offset >= totalSize) { // Offset이 데이터 크기를 초과하면 빈 리스트 반환
            return Collections.emptyList();
        }

        // offset에서 시작하여 size만큼의 데이터를 선택
        int startIndex = offset;
        int endIndex = Math.min(offset + size, totalSize);
        return lists.subList(startIndex, endIndex);
    }

    private List<BookListDto> dtosWithRecommend(List<BookList> lists) {
        var dtosWithRecommend = lists.stream()
            .map(list -> {
                var isWriter = isWriter(list, memberService);
                var favorite = countFavoriteByBookListId(list.getId());
                var comments = countCommentByBookListId(list.getId());
                var bookDtos = list.getBooks().stream()
                    .map(this::toBookDto)
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
                    list.getCount(),
                    favorite,
                    comments
                );
            })
            .toList();
        return dtosWithRecommend;
    }
}
