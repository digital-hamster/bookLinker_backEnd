package com.books_recommend.book_recommend.controller;

import com.books_recommend.book_recommend.common.web.ApiResponse;
import com.books_recommend.book_recommend.dto.ListFavoriteDto;
import com.books_recommend.book_recommend.service.ListFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
class ListFavoriteController {
    private final ListFavoriteService service;

    @PostMapping("/{bookListId}")
    public ApiResponse<PostResponse> create(@PathVariable Long bookListId){
        var dto = service.create(bookListId);
        var response = new PostResponse(
            dto.id()
        );

        return ApiResponse.success(response);
    }

    record PostResponse(
        Long id
    ){}

    @DeleteMapping("/{bookListId}/{favoriteId}")
    public ApiResponse<DeleteResponse> delete(@PathVariable Long bookListId,
                                              @PathVariable Long favoriteId){
        var dto = service.delete(bookListId, favoriteId);
        var response = new DeleteResponse(
            dto.id()
        );

        return ApiResponse.success(response);
    }

    record DeleteResponse(
        Long id
    ){}

    @GetMapping("/{bookListId}")
    public ApiResponse<List<GetResponse>> getByBookList(@PathVariable Long bookListId){
        var dtos = service.getByBookList(bookListId);
        var response = from(dtos);

        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<List<GetResponse>> getByMember(){
        var dtos = service.getByMember();
        var response = from(dtos);

        return ApiResponse.success(response);
    }

    private List<GetResponse> from(List<ListFavoriteDto> dtos) {
        return dtos.stream().map(dto ->
                new GetResponse(
                    dto.id(),
                    dto.memberId(),
                    dto.bookListId()
                ))
            .collect(Collectors.toList());
    }

    record GetResponse(
        Long id,
        Long memberId,
        Long bookListId
    ){}
}
