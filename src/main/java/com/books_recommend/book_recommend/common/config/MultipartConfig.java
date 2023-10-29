package com.books_recommend.book_recommend.common.config;

import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration //이미지 에러 로깅
@EnableConfigurationProperties
public class MultipartConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.servlet.multipart")
    public MultipartProperties multipartProperties() {
        return new MultipartProperties();
    }

    @Bean
    public String validateMultipartConfig(MultipartProperties multipartProperties) {
        System.out.println("Max Request Size: " + multipartProperties.getMaxRequestSize());
        System.out.println("Max File Size: " + multipartProperties.getMaxFileSize());
        return "Multipart configuration validated.";
    }
}