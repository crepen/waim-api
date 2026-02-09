package com.waim.core.common.util.jwt.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JwtConfig {
    private String userName;
    private Long expiration;
    private String secretKey;
}
