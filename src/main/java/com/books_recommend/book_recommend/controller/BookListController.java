package com.books_recommend.book_recommend.controller;

import com.books_recommend.book_recommend.common.web.ApiResponse;
import com.books_recommend.book_recommend.dto.BookDto;
import com.books_recommend.book_recommend.dto.BookListDto;
import com.books_recommend.book_recommend.entity.Book;
import com.books_recommend.book_recommend.service.BookListService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/booklists")
@RequiredArgsConstructor
public class BookListController {
    private final BookListService service;

    @PostMapping("/{memberId}")
    ApiResponse<CreateBookListResponse> createBookList(@RequestBody CreateRequest request
        ,@PathVariable Long memberId){

        var listDto = service.create(request.toRequirement(), memberId);

        var response = new CreateBookListResponse(listDto.bookListId(), listDto.books());

        return ApiResponse.success(response);
    }
    record CreateRequest(
        String title,
        String backImg,
        String content,
        String hashTag,

        List<BooksRequest> books

    ) {
        public BookListService.CreateRequirement toRequirement() {
            var booksRequirements =
                books.stream()
                    .map(BooksRequest::toBooksRequirement)
                    .collect(Collectors.toList());

            return new BookListService.CreateRequirement(
                title,
                backImg,
                content,
                hashTag,
                booksRequirements
            );
        }
    }

    record BooksRequest(
        @NotBlank(message = "책 제목을 입력해 주세요.")
        String title,

        @NotBlank(message = "추천 내용을 입력해 주세요.")
        String content,

        @NotBlank(message = "링크를 기입해 주세요.")
        String link,

        @NotBlank(message = "이미지 파일을 기입해 주세요")
        String image
    ){
        public BookListService.BooksRequirement toBooksRequirement(){
            return new BookListService.BooksRequirement(
                title,
                content,
                link,
                image
            );
        }
    }

    record CreateBookListResponse(
        Long ListId,
        List<BookDto> bookDtos
    ){}



    @GetMapping("/{listId}")
    ApiResponse<GetBookListResponse> getList(@PathVariable Long listId){
        var list = service.getList(listId);
        var response = GetBookListResponse.to(list);
        return ApiResponse.success(response);
    }

    record GetBookListResponse(
        Long listId,
        String title,
        String backImg,
        String content,
        List<BookDto> bookDtos
    ){
        static BookListController.GetBookListResponse to(BookListDto listDto){
            return new BookListController.GetBookListResponse(
                listDto.bookListId(),
                listDto.title(),
                listDto.backImg(),
                listDto.content(),
                listDto.books()
            );
        }
    }
}