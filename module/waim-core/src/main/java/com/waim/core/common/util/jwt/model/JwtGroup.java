package com.waim.core.common.util.jwt.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.waim.core.domain.user.model.dto.BaseUser;
import com.waim.core.domain.user.model.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JwtGroup {

    @JsonProperty("access_token")
    private Item accessToken;

    @JsonProperty("refresh_token")
    private Item refreshToken;

    @JsonProperty("user")
    private BaseUser user;

    @Getter
    @Setter
    @Builder
    public static class Item {
        private String token;
        private Long expires;
    }
}
