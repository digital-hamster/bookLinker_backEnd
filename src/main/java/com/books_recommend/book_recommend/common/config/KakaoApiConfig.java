package com.books_recommend.book_recommend.common.config;


import com.books_recommend.book_recommend.common.properties.KakaoProperties;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(KakaoProperties.class)
@Getter
public class KakaoApiConfig {

    private final String key;
    private final String url;

    public KakaoApiConfig(KakaoProperties kakaoProperties) {
        this.key = kakaoProperties.getKey();
        this.url = "https://dapi.kakao.com/v3/search/book"; // 실제 Kakao API URL을 설정하세요
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
