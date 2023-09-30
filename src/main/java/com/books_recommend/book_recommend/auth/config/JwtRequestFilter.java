package com.books_recommend.book_recommend.auth.config;

import com.books_recommend.book_recommend.common.exception.AuthExceptionCode;
import com.books_recommend.book_recommend.auth.service.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
//@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    //요청당 한번의 filter를 수행하도록 doFilterInternal() 메서드를 구현하는 게 해당 ~ 클래스 목적
    //OncePerRequestFilter: 여러 HTTP 요청 간에 공통적으로 수행해야 하는 작업을 구현할 수 있음
    //ㄴ>인증, 로깅, 캐싱 ~ 등

    private final JwtUserDetailsService jwtUserDetailService;
    private final JwtTokenizer jwtTokenizer;

    @Autowired
    public JwtRequestFilter(
        JwtUserDetailsService jwtUserDetailService,
        JwtTokenizer jwtTokenizer
    ) {
        this.jwtUserDetailService = jwtUserDetailService;
        this.jwtTokenizer = jwtTokenizer;
    }

    //JWT 인증을 건너뛸 URL 패턴 정의
    private static final List<String> EXCLUDE_URL =
        Collections.unmodifiableList(
            Arrays.asList(
                "/members/authenticate",
                "/members"
            ));


    //실제 필터링 작업
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");
        //Authorization 헤더를 읽어옵
        if(requestTokenHeader.isEmpty()){
            AuthExceptionCode.handleException(response, AuthExceptionCode.TOKEN_NOT_FOUND);
            return;
        }

        String username = null;
        String jwtToken = null;

        //이미 인증된 사용자인지 확인
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtTokenizer.getUsernameFromToken(jwtToken);//토큰 파싱 > 유저 정보 추출 (username)
            } catch (IllegalArgumentException e) {
                AuthExceptionCode.handleException(response, AuthExceptionCode.TOKEN_NOT_UNABLE);
                return;
            } catch (ExpiredJwtException e) {
                AuthExceptionCode.handleException(response, AuthExceptionCode.TOKEN_EXPIRED);
            }
        } else {
            AuthExceptionCode.handleException(response, AuthExceptionCode.TOKEN_NOT_BEGIN_BEARER);
        }

        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) { //현재 인증이 여부
            var userDetails = this.jwtUserDetailService.loadUserByUsername(username);

            if(jwtTokenizer.validateToken(jwtToken, userDetails)) { //validate 메소드(util)에 있는 메소드로 토큰 검증
                var authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null ,userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //ㄴ> 요청에 대한 인증 세부 정보를 설정 (여기서 credentails 부여)

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                //ㄴ> Spring Security의 SecurityContextHolder에 인증 정보 설정
            }
        }
        filterChain.doFilter(request,response);
        //ㄴ>  다음 필터 체인으로 요청 전달
    }

    @Override //인증을 건너뛸 URL 패턴이 있는지 확인, 패턴이 있는 경우 필터를 적용하지 않도록 설정
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return EXCLUDE_URL.stream().anyMatch(exclude -> exclude.equalsIgnoreCase(request.getServletPath()));
    }

}