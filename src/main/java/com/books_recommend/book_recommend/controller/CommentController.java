package com.books_recommend.book_recommend.controller;

import com.books_recommend.book_recommend.common.web.ApiResponse;
import com.books_recommend.book_recommend.service.CommentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
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

    @GetMapping("{bookListId}")
    ApiResponse<List<GetResponse>> getComments(@PathVariable Long bookListId){

        var dtos = service.getComments(bookListId);
        var response = dtos.stream()
                .map(dto -> new GetResponse(
                    dto.commentId(),
                    dto.memberId(),
                    dto.bookListId(),
                    dto.content(),
                    dto.createdAt()
                ))
            .toList();

        return ApiResponse.success(response);
    }

    record GetResponse(
        Long commentId,
        Long memberId,
        Long bookListId,
        String content,
        LocalDateTime createAt
//        Boolean isWriter
    ){}

    @PutMapping("{bookListid}/{memberId}")
    ApiResponse<Long> update(@RequestBody UpdateRequest request,
                             @PathVariable Long bookListid,
                             @PathVariable Long memberId){
        var content = request.content;
        var commentId = service.update(content, bookListid, memberId);

        return ApiResponse.success(commentId);
    }

    record UpdateRequest(
        String content
    ){}
}
