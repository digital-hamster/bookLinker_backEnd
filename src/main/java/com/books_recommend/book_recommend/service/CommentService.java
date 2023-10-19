package com.books_recommend.book_recommend.service;

import com.books_recommend.book_recommend.auth.util.SecurityUtil;
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
    private final BookListRepository bookListRepository;
    private final MemberService memberService;



    @Transactional
    public Long create(CreateRequirement requirement, Long bookListId){

        var member = memberService.findMember();
        var bookList = findBookList(bookListId);
        var comment = createComment(requirement, member, bookList);

//        comment.addMember(member);
//        comment.addBookList(bookList);

        var savedComment = commentRepository.save(comment);
        return savedComment.getId();
    }
    private static Comment createComment(CreateRequirement createRequirement, Member member, BookList bookList){
        return new Comment(
            createRequirement.content,
            bookList.getId(),
            member.getId()
        );
    }
    public record CreateRequirement(
        String content
    ){}

    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Long bookListId){
        findBookList(bookListId);

        List<Comment> comments = commentRepository.findAllByBookListId(bookListId);
        var dtos = comments.stream()
            .map(comment -> {
                boolean isWriter = isWriter(comment, memberService);
                return new CommentDto(
                    comment.getId(),
                    comment.getMemberId(),
                    getNickname(comment.getMemberId()),
                    comment.getBookListId(),
                    isWriter,
                    comment.getContent(),
                    comment.getCreatedAt()
                );
            })
            .toList();

        return dtos;
    }
    private String getNickname(Long memberId){
        return memberService.getNicknameById(memberId);
    }

    private static Boolean isWriter(Comment comment, MemberService memberService){
        if (SecurityUtil.hasToken() &&
            memberService.findMember().getId() == comment.getMemberId()){
            return true;
        }
        return false;
    }

    @Transactional
    public void update(String content, Long commentId){
        var member = memberService.findMember();
        findBookListByCommentId(commentId);

        var comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
        checkWriter(comment, member);


        comment.update(content);
        commentRepository.save(comment);
    }

    @Transactional
    public void delete(Long commentId){
        var member = memberService.findMember();
        findBookListByCommentId(commentId);


        var comment = commentRepository.findById(commentId)
            .orElseThrow(()-> new BusinessLogicException(ExceptionCode.LIST_NOT_FOUND));
        checkWriter(comment, member);

        commentRepository.delete(comment);
    }

    private static void checkWriter(Comment comment, Member member){
        if(comment.getMemberId() != member.getId()){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_WRITER);
        }
    }


    //TODO 추후 다른 서비스에도 넣을 내부 메소드@@@@
    private BookList findBookList(Long bookListId) {
        return bookListRepository.findById(bookListId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.LIST_NOT_FOUND));
    }

    private BookList findBookListByCommentId(Long commentId){
        var bookList = bookListRepository.findBookListByCommentId(commentId);
            if (bookList == null){
                throw new BusinessLogicException(ExceptionCode.LIST_NOT_FOUND);
            }
        return bookList;
    }

}
