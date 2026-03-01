package com.waim.module.util.jwt;

import com.waim.module.util.jwt.data.JwtObject;
import com.waim.module.util.jwt.data.JwtResult;
import com.waim.module.util.jwt.data.JwtType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.*;
import java.util.Date;
import java.util.Map;

public class JwtProvider {

    private final SecretKey jwtSecretKey;
    private final Duration accessTokenExpiresIn;
    private final Duration refreshTokenExpiresIn;

    private final String jwtIssuer;

    public JwtProvider(String issuer, String jwtSecretKey, Duration accessTokenExpiresIn, Duration refreshTokenExpiresIn){
        this.jwtIssuer = issuer;
        this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes());
        this.accessTokenExpiresIn = accessTokenExpiresIn;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }


    public JwtResult generateToken(Map<String , ?> claims) {
        return JwtResult.builder()
                .accessToken(makeToken(JwtType.ACCESS_TOKEN, accessTokenExpiresIn, claims))
                .refreshToken(makeToken(JwtType.REFRESH_TOKEN, refreshTokenExpiresIn, claims))
                .build();
    }


    public Map<String , ?> getPayloadData(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(jwtSecretKey)
                    .requireIssuer(jwtIssuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e){
            return null;
        }
    }

    public JwtType getTokenType(String token){
        return JwtType.valueOf(
                getPayloadData(token)
                        .get("token_type")
                        .toString()
        );
    }

    public JwtObject makeToken(JwtType type, Duration expireDuration , Map<String, ?> claims){
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expireAt = now.plus(expireDuration);

        String token = Jwts.builder()
                .issuer(jwtIssuer)
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(expireAt.toInstant()))
                .claims(claims)
                .claim("token_type" , type)
                .signWith(jwtSecretKey)
                .compact();

        return JwtObject.builder()
                .token(token)
                .expiredAt(expireAt.toInstant().toEpochMilli())
                .build();
    }

    public boolean isValid(String token){
        try {
            Jwts.parser()
                    .verifyWith(jwtSecretKey)
                    .requireIssuer(jwtIssuer)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isExpired(String token){
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtSecretKey)
                    .requireIssuer(jwtIssuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

}
