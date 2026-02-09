package com.waim.api.common.config.security.filter;

import com.waim.core.common.model.error.WAIMException;
import com.waim.core.common.util.jwt.JwtTokenProvider;
import com.waim.core.common.util.jwt.model.JwtUserDetail;
import com.waim.core.domain.auth.model.error.AuthErrorCode;
import com.waim.core.domain.auth.model.error.WAIMAuthModuleException;
import com.waim.core.domain.user.model.entity.UserEntity;
import com.waim.core.domain.user.model.error.UserErrorCode;
import com.waim.core.domain.user.repoisitory.UserRepository;
import com.waim.core.domain.user.service.WAIMUserService;
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
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

                String type = tokenProvider.getTokenType(jwt);

                if(type.equals("ACT")){
                    JwtUserDetail userDetail = tokenProvider.getUserDetail(jwt);


                    if(userDetail == null){
                        throw new WAIMException(UserErrorCode.Common.USER_NOT_FOUND);
                    }

                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userDetail,
                            null,
                            userDetail.getUserRole().stream().map(SimpleGrantedAuthority::new).toList()
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            }
        } catch (Exception ex) {
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
