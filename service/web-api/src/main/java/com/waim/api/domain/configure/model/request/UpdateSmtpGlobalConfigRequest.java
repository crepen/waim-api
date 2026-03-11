package com.waim.api.domain.configure.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSmtpGlobalConfigRequest {
    private String smtpEnabled;
    private String host;
    private String port;
    private String username;
    private String password;
    private String fromEmail;
    private String fromName;
    private String authEnabled;
    private String startTlsEnabled;
    private String sslEnabled;
}
