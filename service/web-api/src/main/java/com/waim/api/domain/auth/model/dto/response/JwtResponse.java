package com.waim.api.domain.auth.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JwtResponse {

    private JwtResponse.Token refresh;
    private JwtResponse.Token access;

    @Getter
    @Setter
    @Builder
    public static class Token {
        private String token;

        @JsonProperty("expired_at")
        private Long expiredAt;
    }
}
