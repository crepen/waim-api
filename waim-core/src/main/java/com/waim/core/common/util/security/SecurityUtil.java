package com.waim.core.common.util.security;

import com.waim.core.common.util.jwt.model.JwtUserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {
    public static String getCurrentUserUid() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "SYSTEM"; // 인증 정보가 없는 경우 (초기화 로직 등)
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtUserDetail pnt) {
            return pnt.getUserUid();
        } else if (principal instanceof String) {
            return (String) principal;
        }

        return "UNKNOWN";
    }
}
