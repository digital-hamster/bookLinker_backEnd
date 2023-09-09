package com.books_recommend.book_recommend.controller;

import com.books_recommend.book_recommend.common.web.ApiResponse;
import com.books_recommend.book_recommend.service.CommentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
class CommentController {

    private final CommentService service;

    @PostMapping("{bookListId}/{memberId}")
    ApiResponse<CreateRequest.CreateResponse> createComment(@RequestBody CreateRequest request,
                                          @PathVariable Long bookListId,
                                          @PathVariable Long memberId
                                          ){
        var savedComment = service.create(request.toCreateRequirement(), memberId, bookListId);

        var response = new CreateRequest.CreateResponse(savedComment);
        return ApiResponse.success(response);
    }

    record CreateRequest(
        @NotNull(message = "댓글 입력을 해주세요.")
        String content
    ){
        private CommentService.CreateRequirement toCreateRequirement() {
            return new CommentService.CreateRequirement(
                content
            );
        }
        record CreateResponse(
            Long id
        ){}
    }

//    @GetMapping("{bookListId}/{memberId}")
//    ApiResponse getComments(@PathVariable Long bookListId,
//                            @PathVariable Long memberId){
//
//    }
//
//    record GetResponse(
//        Long commentId,
//        Long content,
//        LocalDateTime createAt,
//        Boolean isWriter
//    ){}
}
