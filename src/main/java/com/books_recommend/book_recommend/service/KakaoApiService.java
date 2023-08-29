package com.books_recommend.book_recommend.service;


import com.books_recommend.book_recommend.common.properties.KakaoProperties;
import com.books_recommend.book_recommend.dto.KakaoBookDto;
import com.books_recommend.book_recommend.entity.KakaoBook;
import com.books_recommend.book_recommend.repository.KakaoBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KakaoApiService {

    private final KakaoProperties kakaoProperties;
    private final RestTemplate restTemplate;
    private final KakaoBookRepository repository;

    @Autowired
    public KakaoApiService(KakaoProperties kakaoProperties, RestTemplate restTemplate, KakaoBookRepository repository) {
        this.kakaoProperties = kakaoProperties;
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    public List<KakaoBookDto> callApiAndSaveToDatabase(String query) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "KakaoAK " + kakaoProperties.getKey());
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        URI targetUrl = UriComponentsBuilder
            .fromUriString(getKakaoUrl())
            .queryParam("query", query)
            .build()
            .encode(StandardCharsets.UTF_8)
            .toUri();

        ResponseEntity<Map> result = restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, Map.class);

        List<Map<String, Object>> bookDataList = (List<Map<String, Object>>) result.getBody().get("documents");
        List<KakaoBook> savedBooks = mapToKakaoBooks(bookDataList);

        updateOrCreateBooks(savedBooks);

        return mapEntitiesToDtos(savedBooks);
    }

    private void updateOrCreateBooks(List<KakaoBook> savedBooks) {
        for (KakaoBook savedBook : savedBooks) {
            KakaoBook existingBook = repository.findByIsbn(savedBook.getIsbn());
            if (existingBook != null) {
                existingBook.updateFrom(savedBook);
                repository.save(existingBook);
            } else {
                repository.save(savedBook);
            }
        }
    }

    private List<KakaoBook> mapToKakaoBooks(List<Map<String, Object>> bookDataList) {
        List<KakaoBook> books = new ArrayList<>();

        if (bookDataList != null) {
            for (Map<String, Object> bookData : bookDataList) {
                String title = (String) bookData.get("title");
                List<String> authors = (List<String>) bookData.get("authors");
                String isbn = (String) bookData.get("isbn");
                String publisher = (String) bookData.get("publisher");
                String thumbnail = (String) bookData.get("thumbnail");
                String url = (String) bookData.get("url");

                KakaoBook kakaoBook = new KakaoBook(title, String.join(", ", authors), isbn, publisher, thumbnail, url);
                books.add(kakaoBook);
            }
        }

        return books;
    }

    private List<KakaoBookDto> mapEntitiesToDtos(List<KakaoBook> entities) {
        return entities.stream()
            .map(entity -> new KakaoBookDto(
                entity.getTitle(),
                entity.getAuthors(),
                entity.getIsbn(),
                entity.getPublisher(),
                entity.getThumbnail(),
                entity.getUrl()
            ))
            .collect(Collectors.toList());
    }


    private String getKakaoUrl() {
        return "https://dapi.kakao.com/v3/search/book";
    }
}