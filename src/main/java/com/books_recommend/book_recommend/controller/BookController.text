package com.books_recommend.book_recommend.controller;

import com.books_recommend.book_recommend.common.web.ApiResponse;
import com.books_recommend.book_recommend.dto.BookDto;
import com.books_recommend.book_recommend.service.BookService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
class BookController {
    private final BookService service;

    @PostMapping("/{memberId}")
    ApiResponse<CreateResponse> createBook(@RequestBody CreateRequest request, @PathVariable Long memberId){
        var bookDto = service.createBook(request.toRequirement(), memberId);

        var response = new CreateResponse(bookDto.id());
        return ApiResponse.success(response);
    }
    record CreateRequest(
        @NotBlank(message = "책 제목을 입력해 주세요.")
        String title,
        @NotBlank(message = "추천 내용을 입력해 주세요.")
        String content,
        @NotBlank(message = "링크를 기입해 주세요.")
        String link,
        @NotBlank(message = "이미지 파일을 기입해 주세요")
        String image
    ){
        public BookService.CreateRequirement toRequirement(){
            return new BookService.CreateRequirement(title,
                content,
                link,
                image);
        }
    }
    record CreateResponse(
        Long bookId
    ){}


    @GetMapping
    ApiResponse<List<GetResponse>> getBooks(){
        var books = service.getBooks();

        var responses = GetResponse.to(books);
        return ApiResponse.success(responses);
    }
    record GetResponse(
        Long bookId,
        Long memberId,
        String title,
        String content,
        String link,
        String image
    ){
        static List<GetResponse> to(List<BookDto> books) {
            return books.stream()
                .map(book -> new GetResponse(
                    book.id(),
                    book.memberId(),
                    book.title(),
                    book.content(),
                    book.link(),
                    book.image()
                ))
                .collect(Collectors.toList());
        }
    }
}
