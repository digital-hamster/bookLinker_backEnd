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

    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Long bookListId){
//        findMember(memberId);
        findBookList(bookListId);

        List<Comment> comments = commentRepository.findAllByBookListId(bookListId);
        var dtos = comments.stream()
            .map(comment -> new CommentDto(
                comment.getId(),
                comment.getMember().getId(),
                comment.getBookList().getId(),
                comment.getContent(),
                comment.getCreatedAt()
                //작성자 여부는 토큰이 만들어진 이후에
            ))
            .toList();

        return dtos;
    }

    @Transactional
    public Long update(String content, Long commentId, Long memberId){
        var member = findMember(memberId);
        var bookList = findBookList(commentRepository, bookListRepository, commentId);
        checkWriter(bookList, member);

        var comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));
        comment.update(content);

        var savedComment = commentRepository.save(comment);
        return savedComment.getId();
    }

    private static BookList findBookList(CommentRepository commentRepository,
                                         BookListRepository bookListRepository,
                                         Long commentId){
        var bookListid = commentRepository.findBookListIds(commentId);
        var bookList = bookListRepository.findById(bookListid)
            .orElseThrow(()-> new BusinessLogicException(ExceptionCode.LIST_NOT_FOUND));
        return bookList;
    }

    private static void checkWriter(BookList bookList, Member member){
        if(bookList.getMember().getId() != member.getId()){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_WRITER);
        }
    }



    //TODO 추후 다른 서비스에도 넣을 내부 메소드@@@@
    private BookList findBookList(Long bookListId){
        return bookListRepository.findById(bookListId)
            .orElseThrow(()-> new BusinessLogicException(ExceptionCode.LIST_NOT_FOUND));
    }

    private Member findMember(Long memberId){
        return memberRepository.findById(memberId)
            .orElseThrow(()-> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

}
