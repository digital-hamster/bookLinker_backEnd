package com.books_recommend.book_recommend.controller;

import com.books_recommend.book_recommend.common.web.ApiResponse;
import com.books_recommend.book_recommend.dto.BookDto;
import com.books_recommend.book_recommend.dto.BookListDto;
import com.books_recommend.book_recommend.service.BookListService;
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

    @PostMapping("/{memberId}")
    ApiResponse<CreateResponse> createBookList(@RequestBody CreateRequest request,
                                               @PathVariable Long memberId) {
        var bookListId = service.create(request.toCreateRequirement(), memberId);

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
        ) {
        }
    }

    record CreateResponse(
        Long bookListId
    ) {
    }

    @GetMapping
    ApiResponse<Page<GetResponse>> getBookLists(Pageable pageable){
        //(default 1) 클라이언트에게 받아온 pageable에서 꺼내야 함
        var editPageable = PageRequest.of(
            pageable.getPageNumber() - 1,
            pageable.getPageSize(),
            pageable.getSort());

        var lists = service.findAllLists(editPageable);
        Page<GetResponse> responses = GetResponse.from(lists);

        return ApiResponse.success(responses);
    }
    record GetResponse(
        Long bookListId,
        Long memberId,
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

    @GetMapping("/{bookListId}")//TODO 추후 토큰이 나올 경우, 토큰을 통해 isWriter 적용 예정
    ApiResponse<GetOneResponse> getList(@PathVariable Long bookListId){

        var listDto = service.getBookList(bookListId);
        GetOneResponse response = new GetOneResponse(
            listDto.bookListId(),
//            listDto.isWriter(),
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
//        Boolean isWriter,
        Long writerId,
        String title,
        String content,
        String hashTag,
        String backImg,
        List<BookDto> books
    ){}

    @DeleteMapping("/{bookListId}/{memberId}")
    ApiResponse<DeleteResponse> remove(@PathVariable Long bookListId,
                                       @PathVariable Long memberId){
        var removedId = service.remove(bookListId, memberId);
        var response = new DeleteResponse(removedId);
        return ApiResponse.success(response);
    }

    record DeleteResponse(
        Long bookListId
    ){}

    @PutMapping("/{bookListId}/{memberId}")
    ApiResponse<UpdateResponse> update(@RequestBody UpdateRequest request,
                                       @PathVariable Long bookListId,
                                       @PathVariable Long memberId){

        var updatedId = service.update(request.toRequirement(), bookListId, memberId);
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
