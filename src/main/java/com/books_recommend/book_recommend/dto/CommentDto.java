package com.books_recommend.book_recommend.dto;

import java.time.LocalDateTime;

public record CommentDto(
    Long commentId,
    Long memberId,
    String content,
    LocalDateTime createdAt,
    Boolean isWriter //작성자 여부를 확인하기 위해서 !!!!!!
) {
}
