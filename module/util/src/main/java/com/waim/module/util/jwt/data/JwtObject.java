package com.waim.module.util.jwt.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JwtObject {
    public String token;
    public Long expiredAt;
}
