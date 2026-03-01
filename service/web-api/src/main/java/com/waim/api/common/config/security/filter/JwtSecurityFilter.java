package com.waim.api.common.config.security.filter;

import com.waim.module.core.common.model.error.ServerException;
import com.waim.module.core.domain.auth.model.error.AuthForbiddenException;
import com.waim.module.core.domain.auth.model.error.AuthNotAllowTokenTypeException;
import com.waim.module.core.domain.auth.model.error.AuthTokenExpireException;
import com.waim.module.core.domain.auth.model.error.AuthTokenInvalidException;
import com.waim.module.core.domain.user.model.entity.UserEntity;
import com.waim.module.core.domain.user.service.UserService;
import com.waim.module.data.common.security.SecurityUserDetail;
import com.waim.module.util.jwt.JwtProvider;
import com.waim.module.util.jwt.data.JwtType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;


@Slf4j
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

    private final JwtProvider tokenProvider;
    private final UserService userService;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String jwt = getJwtFromRequest(request);

        if (!StringUtils.hasText(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }


        try {
            if (!tokenProvider.isValid(jwt)) {
                throw new AuthTokenInvalidException();
            } else if (tokenProvider.isExpired(jwt)) {
                throw new AuthTokenExpireException();
            } else if (tokenProvider.getTokenType(jwt) != JwtType.ACCESS_TOKEN) {
                throw new AuthNotAllowTokenTypeException();
            }

            SecurityUserDetail userDetail = null;

            try {
                Map<String, ?> tokenPayload = tokenProvider.getPayloadData(jwt);

                userDetail = SecurityUserDetail.builder()
                        .uniqueId(tokenPayload.get("uid").toString())
                        .id(tokenPayload.get("user_id").toString())
                        .userName(tokenPayload.get("user_name").toString())
                        .email(tokenPayload.get("user_email").toString())
                        .roles(Arrays.stream(tokenPayload.get("roles").toString().split(",")).toList())
                        .build();
            } catch (Exception ex) {
                throw new AuthTokenInvalidException();
            }

            // Check Active User
//            Optional<UserEntity> findActiveUser = userService.findActiveUser(userDetail.getUniqueId());

//            if (findActiveUser.isEmpty()) {
//                throw new AuthForbiddenException();
//            }

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetail,
                    null,
                    userDetail.getRoles().stream().map(SimpleGrantedAuthority::new).toList()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);


        }
        catch (ServerException ex) {
            SecurityContextHolder.clearContext();
        }
        catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Request의 Authorization 헤더에서 JWT 추출
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
