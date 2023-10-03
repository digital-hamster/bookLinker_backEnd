package com.books_recommend.book_recommend.dto;

import java.time.LocalDateTime;

public record CommentDto(
    Long commentId,
    Long memberId,
    Long bookListId,
    Boolean isCommentWriter,
    String content,
    LocalDateTime createdAt
) {
}
