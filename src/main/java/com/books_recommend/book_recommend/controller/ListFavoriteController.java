package com.books_recommend.book_recommend.controller;

import com.books_recommend.book_recommend.common.web.ApiResponse;
import com.books_recommend.book_recommend.dto.ListFavoriteDto;
import com.books_recommend.book_recommend.service.ListFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favorites")
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
    public ApiResponse<List<GetWriterResponse>> getByBookList(@PathVariable Long bookListId){
        var dtos = service.getByBookList(bookListId);
        var response = fromWriter(dtos);

        return ApiResponse.success(response);
    }

    private List<GetWriterResponse> fromWriter(List<ListFavoriteDto.GetListFavoriteDto> dtos) {
        return dtos.stream().map(dto ->
                new GetWriterResponse(
                    dto.id(),
                    dto.memberId(),
                    dto.isFavorite(),
                    dto.bookListId()
                ))
            .collect(Collectors.toList());
    }

    record GetWriterResponse(
        Long id,
        Long memberId,
        Boolean isFavorite,
        Long bookListId
    ){}


}
