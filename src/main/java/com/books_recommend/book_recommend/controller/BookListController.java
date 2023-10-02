package com.books_recommend.book_recommend.controller;

import com.books_recommend.book_recommend.common.web.ApiResponse;
import com.books_recommend.book_recommend.dto.BookDto;
import com.books_recommend.book_recommend.dto.BookListDto;
import com.books_recommend.book_recommend.service.BookListService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/booklists")
@RequiredArgsConstructor
class BookListController {
    private final BookListService service;

    @PostMapping()
    ApiResponse<CreateResponse> createBookList(@RequestBody CreateRequest request) {
        var bookListId = service.create(request.toCreateRequirement());

        var response = new CreateResponse(bookListId);
        return ApiResponse.success(response);
    }

    record CreateRequest(
        String title,
        String content,
        String hashTag,
        String backImg,
        List<BookRequest> books
    ) {
        private BookListService.CreateRequirement toCreateRequirement() {
            var booksRequirements = books.stream()
                .map(books -> new BookListService.CreateRequirement.BookRequirement(
                    books.title,
                    books.authors,
                    books.isbn,
                    books.publisher,
                    books.image,
                    books.url,
                    books.recommendation
                ))
                .collect(Collectors.toList());

            return new BookListService.CreateRequirement(
                title,
                content,
                hashTag,
                backImg,
                booksRequirements
            );
        }

        record BookRequest(
            String title,
            String authors,
            String isbn,
            String publisher,
            String image,
            String url,
            String recommendation
        ) {}
    }

    record CreateResponse(
        Long bookListId
    ) {
    }

    @GetMapping("/search")
    ApiResponse<Page<BookListController.GetResponse>> getSearchLists(BookListController.SearchRequest request,
                                                                     @NotNull Pageable pageable){
        var requirement = new BookListService.SearchRequirement(request.title);
        var editPageable = setDefault(pageable);

        var lists = service.findSearchLists(requirement, editPageable);
        var responses = BookListController.GetResponse.from(lists);

        return ApiResponse.success(responses);
    }

    record SearchRequest(
        Optional<String> title,
        @NotNull
        Pageable pageable
    ){}

    @GetMapping
    ApiResponse<Page<GetResponse>> getBookLists(Pageable pageable){
        var editPageable = setDefault(pageable);

        var lists = service.findAllLists(editPageable);
        var responses = GetResponse.from(lists);

        return ApiResponse.success(responses);
    }
    record GetResponse(
        Long bookListId,
        Long memberId,
        Boolean isWriter,
        String title,
        String content,
        String hashTag,
        String backImg,
        List<BookDto> books
    ) {
        private static Page<GetResponse> from(Page<BookListDto> listDtos) {
            List<GetResponse> getResponses = listDtos.getContent().stream()
                .map(listDto -> {
                    List<BookDto> bookDtos = listDto.books().stream()
                        .map(book -> new BookDto(
                            book.id(),
                            book.title(),
                            book.authors(),
                            book.isbn(),
                            book.publisher(),
                            book.image(),
                            book.url(),
                            book.recommendation()
                        ))
                        .collect(Collectors.toList());

                    return new GetResponse(
                        listDto.bookListId(),
                        listDto.memberId(),
                        listDto.isWriter(),
                        listDto.title(),
                        listDto.content(),
                        listDto.hashTag(),
                        listDto.backImg(),
                        bookDtos
                    );
                })
                .collect(Collectors.toList());

            return new PageImpl<>(getResponses, listDtos.getPageable(), listDtos.getTotalElements());
        }
    }

    private static Pageable setDefault(Pageable pageable){ //(default 1) 클라이언트에게 받아온 pageable에서 꺼내야 함
        return PageRequest.of(
            pageable.getPageNumber() - 1,
            pageable.getPageSize(),
            pageable.getSort());
    }

    @GetMapping("/{bookListId}")//TODO 추후 토큰이 나올 경우, 토큰을 통해 isWriter 적용 예정
    ApiResponse<GetOneResponse> getList(@PathVariable Long bookListId){

        var listDto = service.getBookList(bookListId);
        GetOneResponse response = new GetOneResponse(
            listDto.bookListId(),
            listDto.isWriter(),
            listDto.memberId(),
            listDto.title(),
            listDto.content(),
            listDto.hashTag(),
            listDto.backImg(),
            listDto.books());

        return ApiResponse.success(response);
    }

    record GetOneResponse(
        Long bookListId,
        Boolean isWriter,
        Long writerId,
        String title,
        String content,
        String hashTag,
        String backImg,
        List<BookDto> books
    ){}

    @DeleteMapping("/{bookListId}")
    ApiResponse<DeleteResponse> remove(@PathVariable Long bookListId){
        var removedId = service.remove(bookListId);
        var response = new DeleteResponse(removedId);
        return ApiResponse.success(response);
    }

    record DeleteResponse(
        Long bookListId
    ){}

    @PutMapping("/{bookListId}")
    ApiResponse<UpdateResponse> update(@RequestBody UpdateRequest request,
                                       @PathVariable Long bookListId){

        var updatedId = service.update(request.toRequirement(), bookListId);
        var response = new UpdateResponse(updatedId);

        return ApiResponse.success(response);
    }
    record UpdateRequest(
        String title,
        String content,
        String hashTag,
        String backImg,
        List<UpdateRequest.UpdateBookRequest> books
    ){
        private BookListService.UpdateRequirement toRequirement() {
            var booksRequirements = books.stream()
                .map(books -> new BookListService.UpdateRequirement.BookRequirement(
                    books.title,
                    books.authors,
                    books.isbn,
                    books.publisher,
                    books.image,
                    books.url,
                    books.recommendation
                ))
                .collect(Collectors.toList());

            return new BookListService.UpdateRequirement(
                title,
                content,
                hashTag,
                backImg,
                booksRequirements
            );
        }

        record UpdateBookRequest(
            String title,
            String authors,
            String isbn,
            String publisher,
            String image,
            String url,
            String recommendation
        ) { }



    }

    record UpdateResponse(
        Long bookListId
    ){}

}
