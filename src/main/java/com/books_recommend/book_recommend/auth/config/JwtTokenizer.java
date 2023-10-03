package com.books_recommend.book_recommend.auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenizer {

    private static final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60;

    //JWT 토큰에서 ID추출
    public String getUsernameFromToken(String token) { //claims에 id만 담았음
        return getClaimFromToken(token, Claims::getId);
    }

    //특정 클레임을 추출
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }


    //(custom) JWT 토큰에서 모든 클레임을 추출
    private Claims getAllClaimsFromToken(String token) {

        // JwtParser를 빌더를 사용하여 생성
        JwtParser parser = Jwts.parserBuilder()
            .setSigningKey(secretKey) // 서명 키를 설정
            .build();
        // parseClaimsJws 메소드를 사용하여 JWS 토큰을 파싱, Claims 객체를 얻음
        var claims = parser.parseClaimsJws(token).getBody();

        return claims;
    }

    //JWT 토큰이 만료되었는지 확인
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //JWT 토큰의 만료 날짜를 추출
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }


    //JWT 토큰의 유효성을 검사 (토큰 만료, 토큰의 사용자 이름이 UserDetails 객체와 일치하는지)
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        //getUsername: 사용자 식별자 의미
    }

    //tokenBuilder
    public String buildToken(Map<String, Object> claims) {
        return Jwts.builder()
            .setClaims(claims)
            .setId((String) claims.get("email"))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 500000000))
            .signWith(secretKey) //ㅇㅣ부분 시크릿키
            .compact();
    }

    //tokenClaims
    public String generateTokenForMember( Long id,
                                          String email,
                                          Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("email", email);
        claims.put("roles", authorities);

        return this.buildToken(claims);
    }

}
