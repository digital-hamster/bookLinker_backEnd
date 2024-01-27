package com.books_recommend.book_recommend.auth.config;

import com.books_recommend.book_recommend.auth.handle.JwtAuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final UserDetailsService userDetailsService;

    private final JwtRequestFilter jwtRequestFilter;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(
        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
        UserDetailsService userDetailsService,
        JwtRequestFilter jwtRequestFilter,
        PasswordEncoder passwordEncoder
    ) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
        this.passwordEncoder = passwordEncoder;
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)

            .authorizeHttpRequests(
                authorize -> authorize
                    .requestMatchers(HttpMethod.POST, "/members/authenticate", "/members", "/favorites", "/booklists").permitAll()
                    .requestMatchers(HttpMethod.POST, "/test/unheaders", "/test/headers").permitAll()
                    .requestMatchers(HttpMethod.GET, "/books", "/booklists/search", "/booklists/**", "/comments/**", "/favorites/**", "/members/favorites", "/booklists/counts", "/booklists/favorites", "/booklists/comments", "/members/oauth-login", "/members/oauth-login/**").permitAll()
                    .requestMatchers(HttpMethod.PUT, "/booklists","booklists/**", "/comments/**").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "booklists/**", "/comments/**", "favorites/**").permitAll()
                    .anyRequest().authenticated()
            )

            .exceptionHandling(customize -> customize
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)// 인증되지 않은 사용자에게 호출(에러핸들러)
                .accessDeniedHandler(customAccessDeniedHandler())
            )

            .sessionManagement(session -> session //Session 미사용
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        http
            .addFilterBefore(new CorsFilter(corsConfigurationSource()), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtRequestFilter, CorsFilter.class);
        //필터 순서 정의: CorsFilter > jwtRequestFilter > UsernamePasswordAuthenticationFilter

        return http.build();
    }


    @Bean //cors 설정 빈등록
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
//        configuration.addExposedHeader("authorization");
        configuration.setExposedHeaders(Arrays.asList("Authorization", "authorization")); //응답헤더
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean //TODO 다른 처리로 변경하기
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Access Denied: You do not have permission to access this resource\"}");
        };
    }
}




