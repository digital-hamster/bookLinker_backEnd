package com.books_recommend.book_recommend.controller;

import com.books_recommend.book_recommend.common.web.ApiResponse;
import com.books_recommend.book_recommend.dto.BookDto;
import com.books_recommend.book_recommend.dto.BookListDto;
import com.books_recommend.book_recommend.service.BookListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    ApiResponse<List<GetResponse>> getBookLists(){
        List<BookListDto> lists = service.findAllLists();
        List<GetResponse> responses = GetResponse.from(lists);

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
        private static List<GetResponse> from(List<BookListDto> listDtos) {
            return listDtos.stream()
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
        }
    }
}
