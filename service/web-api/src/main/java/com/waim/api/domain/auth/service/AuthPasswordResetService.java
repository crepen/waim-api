package com.waim.api.domain.auth.service;

import com.waim.api.common.service.SmtpMailService;
import com.waim.module.core.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.context.i18n.LocaleContextHolder;

@Service
@RequiredArgsConstructor
public class AuthPasswordResetService {
    private final UserService userService;
    private final SmtpMailService smtpMailService;

    public void resetPasswordAndSendMail(String email) {
        String temporaryPassword = userService.resetPasswordByEmail(email);
        smtpMailService.sendTemporaryPasswordMail(email, temporaryPassword, LocaleContextHolder.getLocale());
    }
}
