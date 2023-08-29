package com.books_recommend.book_recommend.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("kakao")
@Getter
@Setter //Failed to bind properties under 'kakao' to com.books_recommend.book_recommend.common.properties.KakaoProperties:
public class KakaoProperties {
    private String key;

    public void applyKey(String key) {
        this.key = key;
    }
}
