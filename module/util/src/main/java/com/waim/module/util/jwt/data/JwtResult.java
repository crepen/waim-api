package com.waim.module.util.jwt.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JwtResult{
    private JwtObject refreshToken;
    private JwtObject accessToken;
}


