package com.books_recommend.book_recommend.service;

import com.books_recommend.book_recommend.common.exception.BusinessLogicException;
import com.books_recommend.book_recommend.common.exception.ExceptionCode;
import com.books_recommend.book_recommend.dto.CommentDto;
import com.books_recommend.book_recommend.entity.BookList;
import com.books_recommend.book_recommend.entity.Comment;
import com.books_recommend.book_recommend.entity.Member;
import com.books_recommend.book_recommend.repository.BookListRepository;
import com.books_recommend.book_recommend.repository.CommentRepository;
import com.books_recommend.book_recommend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final BookListRepository bookListRepository;



    @Transactional
    public Long create(CreateRequirement requirement, Long memberId, Long bookListId){

        var member = findMember(memberId);
        var bookList = findBookList(bookListId);
        var comment = createComment(requirement);

        comment.addMember(member);
        comment.addBookList(bookList);

        var savedComment = commentRepository.save(comment);
        return savedComment.getId();
    }
    private static Comment createComment(CreateRequirement createRequirement){
        return new Comment(
            createRequirement.content
        );
    }
    public record CreateRequirement(
        String content
    ){}





//    @Transactional(readOnly = true)
//    public List<CommentDto> getComments(Long bookListId, Long memberId){
//        var member = findMember(memberId);
//        var bookList = findBookList(bookListId);
//
//        List<Comment> comments = commentRepository.findByBookListIdAll(bookListId);
//    }




    private BookList findBookList(Long bookListId){
        return bookListRepository.findById(bookListId)
            .orElseThrow(()-> new BusinessLogicException(ExceptionCode.LIST_NOT_FOUND));
    }

    private Member findMember(Long memberId){
        return memberRepository.findById(memberId)
            .orElseThrow(()-> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
