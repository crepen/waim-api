package com.waim.api.common.service;

import com.waim.module.core.domain.auth.model.error.AuthSmtpDisabledException;
import com.waim.module.core.domain.auth.model.error.AuthSmtpSendFailedException;
import com.waim.module.core.system.config.service.SystemConfigService;
import com.waim.module.data.system.config.SystemConfigKey;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmtpMailService {

    private static final String DEFAULT_SMTP_ENABLED = "no";
    private static final String DEFAULT_SMTP_HOST = "smtp.example.com";
    private static final String DEFAULT_SMTP_PORT = "587";
    private static final String DEFAULT_SMTP_USERNAME = "noreply@example.com";
    private static final String DEFAULT_SMTP_PASSWORD = "change-me";
    private static final String DEFAULT_SMTP_FROM_EMAIL = "noreply@example.com";
    private static final String DEFAULT_SMTP_FROM_NAME = "WAIM";
    private static final String DEFAULT_SMTP_AUTH_ENABLED = "yes";
    private static final String DEFAULT_SMTP_STARTTLS_ENABLED = "yes";
    private static final String DEFAULT_SMTP_SSL_ENABLED = "no";

    private final SystemConfigService systemConfigService;
    private final MessageSource messageSource;

    public void sendTemporaryPasswordMail(String recipientEmail, String temporaryPassword, Locale locale) {
        sendLocalizedMail(
                recipientEmail,
                "waim.mail.password_reset.subject",
                "waim.mail.password_reset.body",
                locale,
                temporaryPassword
        );
    }

    public void sendSignupPendingApprovalMail(String recipientEmail, Locale locale) {
        sendLocalizedMail(
                recipientEmail,
                "waim.mail.signup.pending.subject",
                "waim.mail.signup.pending.body",
                locale
        );
    }

    public void sendSignupCompletedMail(String recipientEmail, Locale locale) {
        sendLocalizedMail(
                recipientEmail,
                "waim.mail.signup.completed.subject",
                "waim.mail.signup.completed.body",
                locale
        );
    }

    public void sendSignupApprovedMail(String recipientEmail, Locale locale) {
        sendLocalizedMail(
                recipientEmail,
                "waim.mail.signup.approved.subject",
                "waim.mail.signup.approved.body",
                locale
        );
    }

    public void sendUserBlockedMail(String recipientEmail, Locale locale) {
        sendLocalizedMail(
                recipientEmail,
                "waim.mail.user.blocked.subject",
                "waim.mail.user.blocked.body",
                locale
        );
    }

    private void sendLocalizedMail(String recipientEmail, String subjectKey, String bodyKey, Locale locale, Object... bodyArgs) {
        if (!isEnabled(SystemConfigKey.SMTP_ENABLED.name(), DEFAULT_SMTP_ENABLED)) {
            throw new AuthSmtpDisabledException();
        }

        String host = getConfig(SystemConfigKey.SMTP_HOST.name(), DEFAULT_SMTP_HOST);
        String port = getConfig(SystemConfigKey.SMTP_PORT.name(), DEFAULT_SMTP_PORT);
        String username = getConfig(SystemConfigKey.SMTP_USERNAME.name(), DEFAULT_SMTP_USERNAME);
        String password = getConfig(SystemConfigKey.SMTP_PASSWORD.name(), DEFAULT_SMTP_PASSWORD);
        String fromEmail = getConfig(SystemConfigKey.SMTP_FROM_EMAIL.name(), DEFAULT_SMTP_FROM_EMAIL);
        String fromName = getConfig(SystemConfigKey.SMTP_FROM_NAME.name(), DEFAULT_SMTP_FROM_NAME);

        boolean authEnabled = isEnabled(SystemConfigKey.SMTP_AUTH_ENABLED.name(), DEFAULT_SMTP_AUTH_ENABLED);
        boolean startTlsEnabled = isEnabled(SystemConfigKey.SMTP_STARTTLS_ENABLED.name(), DEFAULT_SMTP_STARTTLS_ENABLED);
        boolean sslEnabled = isEnabled(SystemConfigKey.SMTP_SSL_ENABLED.name(), DEFAULT_SMTP_SSL_ENABLED);

        Locale targetLocale = (locale == null ? Locale.KOREAN : Locale.of(locale.getLanguage()));
        String subject = messageSource.getMessage(subjectKey, null, subjectKey, targetLocale);
        String body = messageSource.getMessage(bodyKey, bodyArgs, bodyKey, targetLocale);

        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);
            properties.put("mail.smtp.auth", String.valueOf(authEnabled));
            properties.put("mail.smtp.starttls.enable", String.valueOf(startTlsEnabled));
            properties.put("mail.smtp.ssl.enable", String.valueOf(sslEnabled));

            Session session;

            if (authEnabled) {
                session = Session.getInstance(properties, new jakarta.mail.Authenticator() {
                    @Override
                    protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                        return new jakarta.mail.PasswordAuthentication(username, password);
                    }
                });
            }
            else {
                session = Session.getInstance(properties);
            }

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail, fromName, StandardCharsets.UTF_8.name()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));
            message.setSubject(subject, StandardCharsets.UTF_8.name());
            message.setText(body, StandardCharsets.UTF_8.name());

            Transport.send(message);
        }
        catch (Exception ex) {
            log.error("Failed to send mail. recipient={}", recipientEmail, ex);
            throw new AuthSmtpSendFailedException();
        }
    }

    private String getConfig(String key, String defaultValue) {
        return systemConfigService.getConfig(key)
                .map(config -> config.getConfigValue())
                .filter(StringUtils::hasText)
                .orElse(defaultValue);
    }

    private boolean isEnabled(String key, String defaultValue) {
        String value = getConfig(key, defaultValue);
        return "yes".equalsIgnoreCase(value)
                || "true".equalsIgnoreCase(value)
                || "y".equalsIgnoreCase(value)
                || "1".equals(value);
    }
}