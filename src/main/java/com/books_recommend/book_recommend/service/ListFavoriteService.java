package com.books_recommend.book_recommend.service;

import com.books_recommend.book_recommend.auth.util.SecurityUtil;
import com.books_recommend.book_recommend.common.exception.BusinessLogicException;
import com.books_recommend.book_recommend.common.exception.ExceptionCode;
import com.books_recommend.book_recommend.dto.ListFavoriteDto;
import com.books_recommend.book_recommend.entity.ListFavorite;
import com.books_recommend.book_recommend.repository.ListFavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListFavoriteService {
    private final ListFavoriteRepository favoriteRepository;
    private final MemberService memberService;
    private final BookListService bookListService;

    @Transactional
    public ListFavoriteDto create(Long bookListId) {
        var member = memberService.findMember();
        bookListService.findBookListById(bookListId);

        var favorite = new ListFavorite(
            member.getId(),
            bookListId
        );
        isFavorted(member.getId(), bookListId, favoriteRepository);

        var savedFavorite = favoriteRepository.save(favorite);

        var response = new ListFavoriteDto(
            savedFavorite.getId(),
            member.getId(),
            bookListId
        );

        return response;
    }

    private static void isFavorted(Long memberId, Long bookListId, ListFavoriteRepository repository){
        var count = repository.countByMemberIdAndBookListId(memberId, bookListId);
        if(count>0){
            throw new BusinessLogicException(ExceptionCode.FAVORITE_EXISTED);
        }
    }

    @Transactional
    public ListFavoriteDto delete(Long bookListId,
                                  Long listFavoriteId) {
        var member = memberService.findMember();
        bookListService.findBookListById(bookListId);
        var favorite = getFavorite(listFavoriteId);
        verifyFavorite(favorite, member.getId());

        favoriteRepository.delete(favorite);

        return new ListFavoriteDto(
            favorite.getId(),
            favorite.getMemberId(),
            favorite.getBookListId()
        );
    }
    private static void verifyFavorite(ListFavorite favorite, Long memberId){
        if(favorite.getMemberId() != memberId){
            throw new BusinessLogicException(ExceptionCode.FAVORITE_MEMBER_INCONSISTENCY);
        }
    }

    private ListFavorite getFavorite(Long listFavoriteId) {
        SecurityUtil.hasToken();
        var favorite = favoriteRepository.findById(listFavoriteId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.FAVORITE_NOT_FOUND));

        return favorite;
    }

    @Transactional(readOnly = true)
    public List<ListFavoriteDto.GetListFavoriteDto> getByBookList(Long bookListId) {
        bookListService.findBookListById(bookListId);

        var favorites = favoriteRepository.findByBookListId(bookListId);
        var dtos = dtosWithWriter(favorites);

        return dtos;
    }

    private List<ListFavoriteDto.GetListFavoriteDto> dtosWithWriter(List<ListFavorite> favorites) {
        return favorites.stream()
            .map(favorite -> {
                Boolean isFavorite = isWriter(favorite, memberService);
                return new ListFavoriteDto.GetListFavoriteDto(
                    favorite.getId(),
                    favorite.getMemberId(),
                    isFavorite,
                    favorite.getBookListId()
                );
            })
            .collect(Collectors.toList());
    }

    private static Boolean isWriter(ListFavorite favorite,
                                    MemberService memberService) {
        if (SecurityUtil.hasToken() &&
            memberService.findMember().getId() == favorite.getMemberId()) {
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<ListFavoriteDto> getByMember() {
        var member = memberService.findMember();
        var favorites = favoriteRepository.findByMemberId(member.getId());

        var dtos = favorites.stream()
            .map(favorite -> new ListFavoriteDto(
                favorite.getId(),
                favorite.getMemberId(),
                favorite.getBookListId()
            ))
            .collect(Collectors.toList());

        return dtos;
    }

//    public List<Long> getByListIdsDESCFavorite(int offset, int size) {
//        List<Long> bookListIds = favoriteRepository.findBookListIdsByFavoriteDesc();
//        return bookListIds;}

}
