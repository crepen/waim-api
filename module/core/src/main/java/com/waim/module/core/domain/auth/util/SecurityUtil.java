package com.waim.module.core.domain.auth.util;

import com.waim.module.data.common.security.SecurityUserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtil {
    public static Optional<SecurityUserDetail> getUserData() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof SecurityUserDetail sud) {
                return Optional.of(sud);
            }
            else{
                return Optional.empty();
            }
        }

        return Optional.empty();
    }


}
