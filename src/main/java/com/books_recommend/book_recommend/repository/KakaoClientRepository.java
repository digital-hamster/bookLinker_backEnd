package com.books_recommend.book_recommend.repository;

import com.books_recommend.book_recommend.common.properties.KakaoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KakaoClientRepository{
    private final KakaoProperties kakaoProperties;
    private final RestTemplate restTemplate;
    public List<Map<String, Object>> getKakaoBook(String query){
        HttpHeaders httpHeaders = new HttpHeaders(); //준비물이 레포지토리에 잇는거임 ㄷㄷㄷ;;;;;;; 헐랭
        httpHeaders.set("Authorization", "KakaoAK " + kakaoProperties.getKey()); //얘가 ~ http 정보를 준비하는거임 아 1!
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

        URI targetUrl = UriComponentsBuilder
            .fromUriString(getKakaoUrl())
            .queryParam("query", query)
            .build()
            .encode(StandardCharsets.UTF_8)
            .toUri();

        ResponseEntity<Map> result = restTemplate.exchange(targetUrl, HttpMethod.GET, httpEntity, Map.class);

        List<Map<String, Object>> bookDataList = (List<Map<String, Object>>) result.getBody().get("documents");

        return bookDataList;
    }

    private String getKakaoUrl() {
        return "https://dapi.kakao.com/v3/search/book";
    }
}
