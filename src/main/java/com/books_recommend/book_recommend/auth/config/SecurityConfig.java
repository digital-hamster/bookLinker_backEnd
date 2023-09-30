package com.books_recommend.book_recommend.auth.config;

import com.books_recommend.book_recommend.auth.handle.JwtAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@RequiredArgsConstructor
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
                    .requestMatchers("members/authenticate", "/members", "/members/udpate").permitAll()
                    .anyRequest().authenticated()
            )

            .exceptionHandling(customize -> customize
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                // 인증되지 않은 사용자에게 호출(에러핸들러)
            )

            .sessionManagement(session -> session //Session 미사용
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        //제일 먼저 필터를 돈다 (모든 토큰 요청은 해당 필터를 제일 먼저 거침)

        return http.build();
    }
}




