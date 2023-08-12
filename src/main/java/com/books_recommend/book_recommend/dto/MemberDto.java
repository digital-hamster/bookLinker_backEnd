package com.books_recommend.book_recommend.dto;

import com.books_recommend.book_recommend.entity.Member;

public record MemberDto (
    Long id,
    String nickName,
    Member.MemberStatus status
    ){
    public static MemberDto fromEntity(Member member){
        return new MemberDto(
                member.getId(),
                member.getNickName(),
                member.getStatus()
        );
    }
}
