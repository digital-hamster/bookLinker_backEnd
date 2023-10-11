package com.books_recommend.book_recommend.service;

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
    public ListFavoriteDto create(Long bookListId){
        var member = memberService.findMember();
        bookListService.findBookListById(bookListId);

        var favorite = new ListFavorite(
            member.getId(),
            bookListId
        );
        var savedFavorite = favoriteRepository.save(favorite);

        var response = new ListFavoriteDto(
            savedFavorite.getId(),
            member.getId(),
            bookListId
        );

        return response;
    }

    @Transactional
    public ListFavoriteDto delete(Long bookListId, Long listFavoriteId){
        var member = memberService.findMember();
        var bookList = bookListService.findBookListById(bookListId);
        varifyListFavorite(listFavoriteId, bookListId);

        return new ListFavoriteDto(listFavoriteId, member.getId(), bookList.getId());
    }

    private void varifyListFavorite(Long listFavoriteId, Long bookListId){
        var bookListFavoriteId = favoriteRepository.findIdByBookListId(bookListId);
        if(bookListFavoriteId != listFavoriteId){
            throw new BusinessLogicException(ExceptionCode.FAVORITE_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    public List<ListFavoriteDto> getByBookList(Long bookListId){
        bookListService.findBookListById(bookListId);

        var favorites = favoriteRepository.findByBookListId(bookListId);
        var dtos = from(favorites);

        return dtos;
    }

    private List<ListFavoriteDto> from(List<ListFavorite> favorites) {
        return favorites.stream()
            .map(favorite -> new ListFavoriteDto(
                favorite.getId(),
                favorite.getMemberId(),
                favorite.getBookListId()
            ))
            .collect(Collectors.toList());
    }

    private List<ListFavorite> getFavorites(List<Long> listFavoriteIds) {
        return listFavoriteIds.stream()
            .map(this::getFavorite)
            .collect(Collectors.toList());
    }

    private ListFavorite getFavorite(Long listFavoriteId){
        return favoriteRepository.findById(listFavoriteId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.FAVORITE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ListFavoriteDto> getByMember(){
        var member = memberService.findMember();
        var favorites = favoriteRepository.findByMemberId(member.getId());

        var dtos = from(favorites);

        return dtos;
    }
}
