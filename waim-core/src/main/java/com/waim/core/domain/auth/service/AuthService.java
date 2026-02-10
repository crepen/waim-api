package com.waim.core.domain.auth.service;

import com.waim.core.common.model.error.WAIMException;
import com.waim.core.common.util.crypto.CryptoProvider;
import com.waim.core.common.util.jwt.JwtTokenProvider;
import com.waim.core.common.util.jwt.model.JwtGroup;
import com.waim.core.common.util.jwt.model.JwtUserDetail;
import com.waim.core.domain.auth.model.error.AuthErrorCode;
import com.waim.core.domain.auth.model.error.WAIMAuthModuleException;
import com.waim.core.domain.auth.model.error.WAIMLoginValidateException;
import com.waim.core.domain.auth.model.error.WAIMRefreshTokenValidateException;
import com.waim.core.domain.user.model.UserState;
import com.waim.core.domain.user.model.dto.BaseUser;
import com.waim.core.domain.user.model.entity.UserEntity;
import com.waim.core.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final CryptoProvider cryptoProvider;

    public JwtGroup loginUser(String id , String password) {

        if(!StringUtils.hasText(id)){
            throw WAIMLoginValidateException.INVALID_ID;
        }
        else if(!StringUtils.hasText(password)){
            throw WAIMLoginValidateException.INVALID_PASSWORD;
        }

        Optional<UserEntity> findUser = userService.getLoginUserByIdOrEmail(id);

        if(findUser.isEmpty()){
            throw WAIMLoginValidateException.NOT_FOUND_USER;
        }
        else if(!passwordEncoder.matches(password, findUser.get().getUserPassword())){
            throw WAIMLoginValidateException.PASSWORD_NOT_MATCH;
        }
        else if(findUser.get().getUserState() != UserState.ACTIVE){
            switch (findUser.get().getUserState()){
                case PENDING: throw new WAIMException(AuthErrorCode.Validate.LOGIN_FAILED_USER_STATE_PENDING);
                case WITHDRAWN: throw new WAIMException(AuthErrorCode.Validate.LOGIN_FAILED_USER_STATE_WITHDRAWN);
                case SUSPENDED: throw new WAIMException(AuthErrorCode.Validate.LOGIN_FAILED_USER_STATE_SUSPENDED);
                default: throw new WAIMException(AuthErrorCode.Validate.LOGIN_FAILED_USER_STATE_UNKNOWN);
            }
        }
        else {
            JwtGroup.Item accessToken = jwtTokenProvider.generateAccessToken(findUser.get());
            JwtGroup.Item refreshToken = jwtTokenProvider.generateRefreshToken(findUser.get());

            return JwtGroup.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(
                            BaseUser.builder()
                                    .id(findUser.get().getUserId())
                                    .name(findUser.get().getUserName())
                                    .email(cryptoProvider.decrypt(findUser.get().getUserEmail()))
                                    .build()
                    )
                    .build();
        }

    }

    public JwtGroup refreshJwtGroup(String refreshToken) throws WAIMException {
        if(!jwtTokenProvider.validateToken(refreshToken)){
            throw WAIMAuthModuleException.INVALID_TOKEN;
        }
        else if(jwtTokenProvider.isTokenExpired(refreshToken)){
            throw WAIMRefreshTokenValidateException.EXPIRED_REFRESH_TOKEN;
        }
        else if(!jwtTokenProvider.getTokenType(refreshToken).equals("RFT")){
            throw WAIMRefreshTokenValidateException.UNMATCHED_TOKEN_TYPE;
        }

        try{
            JwtUserDetail detail= jwtTokenProvider.getUserDetail(refreshToken);

            if(detail == null){
                throw WAIMAuthModuleException.INVALID_TOKEN;
            }

            Optional<UserEntity> matchUser = userService.getUserByUid(detail.getUserUid());

            if(matchUser.isEmpty()){
                throw WAIMRefreshTokenValidateException.NOT_FOUND_USER;
            }

            JwtGroup.Item accessToken = jwtTokenProvider.generateAccessToken(matchUser.get());
            JwtGroup.Item refToken = jwtTokenProvider.generateRefreshToken(matchUser.get());

            return JwtGroup.builder()
                    .accessToken(accessToken)
                    .refreshToken(refToken)
                    .user(
                            BaseUser.builder()
                                    .id(matchUser.get().getUserId())
                                    .name(matchUser.get().getUserName())
                                    .build()
                    )
                    .build();
        }
        catch (WAIMAuthModuleException e){
            throw e;
        }
        catch (Exception ex){
            throw WAIMException.INTERNAL_SERVER_ERROR;
        }

    }

}
