package com.books_recommend.book_recommend.controller;

import com.books_recommend.book_recommend.service.ListFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
class ListFavoriteController {
    private final ListFavoriteService service;

    @PostMapping("/{bookListId}")
    public PostResponse create(@PathVariable Long bookListId){
        service.create(bookListId);


    }

    record PostResponse(
        Long id
    ){}
}
