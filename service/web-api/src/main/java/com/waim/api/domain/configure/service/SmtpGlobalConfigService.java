package com.waim.api.domain.configure.service;

import com.waim.api.domain.configure.model.request.UpdateSmtpGlobalConfigRequest;
import com.waim.module.core.system.config.model.error.SystemConfigSmtpConnectionFailedException;
import com.waim.module.core.system.config.service.SystemConfigService;
import com.waim.module.data.system.config.SystemConfigKey;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmtpGlobalConfigService {

    private final SystemConfigService systemConfigService;

    @Transactional
    public void validateAndSave(UpdateSmtpGlobalConfigRequest reqBody) {
        boolean smtpEnabled = toBoolean(reqBody.getSmtpEnabled());
        boolean authEnabled = toBoolean(reqBody.getAuthEnabled());
        boolean startTlsEnabled = toBoolean(reqBody.getStartTlsEnabled());
        boolean sslEnabled = toBoolean(reqBody.getSslEnabled());

        int port;

        try {
            port = Integer.parseInt(reqBody.getPort());
        }
        catch (Exception ex) {
            throw new SystemConfigSmtpConnectionFailedException();
        }

        if (smtpEnabled) {
            testSmtpConnection(
                    reqBody.getHost(),
                    port,
                    reqBody.getUsername(),
                    reqBody.getPassword(),
                    authEnabled,
                    startTlsEnabled,
                    sslEnabled
            );
        }

        systemConfigService.setConfig(SystemConfigKey.SMTP_ENABLED.name(), normalizeYn(reqBody.getSmtpEnabled()));
        systemConfigService.setConfig(SystemConfigKey.SMTP_HOST.name(), reqBody.getHost());
        systemConfigService.setConfig(SystemConfigKey.SMTP_PORT.name(), String.valueOf(port));
        systemConfigService.setConfig(SystemConfigKey.SMTP_USERNAME.name(), reqBody.getUsername());
        systemConfigService.setConfig(SystemConfigKey.SMTP_PASSWORD.name(), reqBody.getPassword());
        systemConfigService.setConfig(SystemConfigKey.SMTP_FROM_EMAIL.name(), reqBody.getFromEmail());
        systemConfigService.setConfig(SystemConfigKey.SMTP_FROM_NAME.name(), reqBody.getFromName());
        systemConfigService.setConfig(SystemConfigKey.SMTP_AUTH_ENABLED.name(), normalizeYn(reqBody.getAuthEnabled()));
        systemConfigService.setConfig(SystemConfigKey.SMTP_STARTTLS_ENABLED.name(), normalizeYn(reqBody.getStartTlsEnabled()));
        systemConfigService.setConfig(SystemConfigKey.SMTP_SSL_ENABLED.name(), normalizeYn(reqBody.getSslEnabled()));
    }

    public void validateOnly(UpdateSmtpGlobalConfigRequest reqBody) {
        boolean authEnabled = toBoolean(reqBody.getAuthEnabled());
        boolean startTlsEnabled = toBoolean(reqBody.getStartTlsEnabled());
        boolean sslEnabled = toBoolean(reqBody.getSslEnabled());

        int port;

        try {
            port = Integer.parseInt(reqBody.getPort());
        }
        catch (Exception ex) {
            throw new SystemConfigSmtpConnectionFailedException();
        }

        testSmtpConnection(
                reqBody.getHost(),
                port,
                reqBody.getUsername(),
                reqBody.getPassword(),
                authEnabled,
                startTlsEnabled,
                sslEnabled
        );
    }

    private void testSmtpConnection(
            String host,
            int port,
            String username,
            String password,
            boolean authEnabled,
            boolean startTlsEnabled,
            boolean sslEnabled
    ) {
        if (!StringUtils.hasText(host) || port < 1 || port > 65535) {
            throw new SystemConfigSmtpConnectionFailedException();
        }

        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", String.valueOf(port));
            props.put("mail.smtp.auth", String.valueOf(authEnabled));
            props.put("mail.smtp.starttls.enable", String.valueOf(startTlsEnabled));
            props.put("mail.smtp.ssl.enable", String.valueOf(sslEnabled));
            props.put("mail.smtp.connectiontimeout", "5000");
            props.put("mail.smtp.timeout", "5000");
            props.put("mail.smtp.writetimeout", "5000");

            Session session = Session.getInstance(props);

            try (Transport transport = session.getTransport("smtp")) {
                if (authEnabled) {
                    transport.connect(host, port, username, password);
                }
                else {
                    transport.connect(host, port, null, null);
                }
            }
        }
        catch (Exception ex) {
            log.error("SMTP connection test failed.", ex);
            throw new SystemConfigSmtpConnectionFailedException();
        }
    }

    private boolean toBoolean(String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }

        String normalized = value.trim().toLowerCase();
        return normalized.equals("yes") || normalized.equals("true") || normalized.equals("y") || normalized.equals("1");
    }

    private String normalizeYn(String value) {
        return toBoolean(value) ? "yes" : "no";
    }
}
