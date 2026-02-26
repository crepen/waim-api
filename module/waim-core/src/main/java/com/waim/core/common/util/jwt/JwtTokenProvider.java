package com.waim.core.common.util.jwt;

import com.waim.core.domain.user.model.entity.UserRoleEntity;
import com.waim.core.common.util.jwt.model.JwtGroup;
import com.waim.core.common.util.jwt.model.JwtUserDetail;
import com.waim.core.domain.user.model.entity.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class JwtTokenProvider {

    private final SecretKey key;
    private final long tokenExpiration;
    private final long refreshTokenExpiration;

    /**
     * JwtTokenProvider 생성자
     * @param secret JWT 서명 키 (최소 256비트 / 32자 이상)
     * @param tokenExpiration Access Token 만료 시간 (밀리초)
     * @param refreshTokenExpiration Refresh Token 만료 시간 (밀리초)
     */
    public JwtTokenProvider(String secret, long tokenExpiration, long refreshTokenExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.tokenExpiration = tokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * Access Token 생성
     */
    public JwtGroup.Item generateAccessToken(UserEntity userEntity) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenExpiration);

        String jwtStr = Jwts.builder()
                .subject(userEntity.getUid())
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("uid" , userEntity.getUid())
                .claim("user_name" , userEntity.getUserName())
                .claim("user_role" , userEntity.getRoles().stream().map(UserRoleEntity::getRole).toList())
                .claim("type" , "ACT")
                .signWith(key)
                .compact();

        return JwtGroup.Item.builder()
                .token(jwtStr)
                .expires(expiryDate.getTime())
                .build();
    }

    /**
     * Refresh Token 생성
     */
    public JwtGroup.Item generateRefreshToken(UserEntity userEntity) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);

        String jwtStr = Jwts.builder()
                .subject(userEntity.getUid())
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("uid" , userEntity.getUid())
                .claim("user_name" , userEntity.getUserName())
                .claim("user_role" , userEntity.getRoles().stream().map(UserRoleEntity::getRole).toList())
                .claim("type" , "RFT")
                .signWith(key)
                .compact();

        return JwtGroup.Item.builder()
                .token(jwtStr)
                .expires(expiryDate.getTime())
                .build();
    }

    /**
     * Token Type 조회 (AFT , RFT)
     *
     * @param token
     * @return
     */
    public String getTokenType(String token) {
        try {


            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();


            String type = claims.get("type", String.class);

            if(!type.equals("ACT") && !type.equals("RFT")) {
                return null;
            }

            return type;

        } catch (Exception e){
            return null;
        }
    }

    public JwtUserDetail getUserDetail(String token)  {
        try {


            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();


            String uid = claims.get("uid", String.class);
            String userName = claims.get("user_name", String.class);

            List<?> rawRoles = claims.get("user_role", List.class);
            List<String> userRoles = (rawRoles != null)
                    ? rawRoles.stream()
                    .map(Object::toString)
                    .toList()
                    : Collections.emptyList();

            return JwtUserDetail.builder()
                    .userUid(uid)
                    .userName(userName)
                    .userRole(userRoles)
                    .build();

        } catch (Exception e){
            return null;
        }
    }


    /**
     * JWT 토큰 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * JWT 토큰 만료 여부 확인
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Token에서 만료 시간 조회
     */
    public long getExpirationTime(String token)  {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().getTime();
        } catch (JwtException | IllegalArgumentException e) {
            return -1;
        }
    }
}