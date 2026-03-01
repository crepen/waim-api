package com.waim.module.core.domain.auth.service;

import com.waim.module.core.domain.auth.model.error.*;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.core.domain.user.model.error.UserBlockException;
import com.waim.module.core.domain.user.model.error.UserEmptyIdException;
import com.waim.module.core.domain.user.model.error.UserEmptyPasswordException;
import com.waim.module.core.domain.user.model.error.UserNotFoundException;
import com.waim.module.core.domain.user.service.UserService;
import com.waim.module.data.domain.user.UserStatus;
import com.waim.module.util.jwt.JwtProvider;
import com.waim.module.util.jwt.data.JwtResult;
import com.waim.module.util.jwt.data.JwtType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public JwtResult login(String idOrEmail , String password){

        if(!StringUtils.hasText(idOrEmail)){
            throw new UserEmptyIdException();
        }

        if(!StringUtils.hasText(password)){
            throw new UserEmptyPasswordException();
        }

        Optional<UserEntity> findUser = userService.findActiveUserByIdOrEmail(idOrEmail);

        if(findUser.isEmpty() || findUser.get().getUserStatus() == UserStatus.DELETE){
            throw new UserNotFoundException();
        }

        if(findUser.get().getUserStatus() == UserStatus.BLOCK){
            throw new UserBlockException();
        }

        if(passwordEncoder.matches(password, findUser.get().getUserPassword())){
            // 비밀번호 보안 업그레이드
            if(passwordEncoder.upgradeEncoding(findUser.get().getUserPassword())){
                userService.updateUserPassword(findUser.get() , password);
            }
        }
        else{
            throw new AuthWrongPasswordException();
        }


        return jwtProvider.generateToken(getUserClaims(findUser.get()));
    }

    @Transactional
    public JwtResult renewToken(String token){
        if(!StringUtils.hasText(token)){
            throw new AuthTokenInvalidException();
        }
        String refToken = token.replaceAll("Bearer" , "").trim();

        if(!jwtProvider.isValid(refToken)){
            throw new AuthTokenInvalidException();
        }
        else if(jwtProvider.isExpired(refToken)){
            throw new AuthTokenExpireException();
        }
        else if(jwtProvider.getTokenType(refToken) != JwtType.REFRESH_TOKEN){
            throw new AuthNotAllowTokenTypeException();
        }

        Map<String , ?> claims = jwtProvider.getPayloadData(refToken);
        String userUid = claims.get("uid").toString();

        Optional<UserEntity> findUser = userService.findUser(userUid);

        if(findUser.isEmpty() || findUser.get().getUserStatus() != UserStatus.ACTIVE){
            throw new AuthTokenInvalidException();
        }


        return jwtProvider.generateToken(getUserClaims(findUser.get()));
    }


    private Map<String , String> getUserClaims(UserEntity userEntity){
        Map<String , String> claims = new HashMap<>();
        claims.put("uid" , userEntity.getUid());
        claims.put("user_name" , userEntity.getUserName());
        claims.put("user_email" , userEntity.getUserEmail());
        claims.put("user_id" , userEntity.getUserId());
        claims.put("roles" , userEntity.getUserRole().name());

        return claims;
    }
}
