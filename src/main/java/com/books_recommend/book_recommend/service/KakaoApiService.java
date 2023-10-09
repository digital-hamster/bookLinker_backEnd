package com.books_recommend.book_recommend.service;

import com.books_recommend.book_recommend.dto.KakaoBookDto;
import com.books_recommend.book_recommend.repository.KakaoClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoApiService {
    private final KakaoClientRepository kakaoClientRepository;

    public List<KakaoBookDto> searchBooks(String query) {
        var bookDataList = kakaoClientRepository.getKakaoBook(query);

        return mapToKakaoBookDtos(bookDataList);
    }

    private List<KakaoBookDto> mapToKakaoBookDtos(List<Map<String, Object>> bookDataList) {
        List<KakaoBookDto> bookDtos = new ArrayList<>();

        if (bookDataList != null) {
            for (Map<String, Object> bookData : bookDataList) {
                String title = (String) bookData.get("title");
                List<String> authors = (List<String>) bookData.get("authors");
                String isbn = (String) bookData.get("isbn");
                String publisher = (String) bookData.get("publisher");
                String image = (String) bookData.get("thumbnail");
                String url = (String) bookData.get("url");

                KakaoBookDto bookDto = new KakaoBookDto(title, authors, isbn, publisher, image, url);
                bookDtos.add(bookDto);
            }
        }

        return bookDtos;
    }
}