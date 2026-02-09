package com.waim.api.domain.auth.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.waim.api.domain.auth.model.dto.WAIMAuthGrantType;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


public class IssuanceTokenRequest {

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXISTING_PROPERTY,
            property = "grant_type",
            visible = true
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Login.class, name = "login"),
            @JsonSubTypes.Type(value = Refresh.class, name = "refresh")
    })
    @Schema(
            description = "토큰 요청 타입",
            oneOf = {IssuanceTokenRequest.Login.class, IssuanceTokenRequest.Refresh.class},
            discriminatorProperty = "grant_type",
            discriminatorMapping = {
                    @io.swagger.v3.oas.annotations.media.DiscriminatorMapping(value = "login", schema = IssuanceTokenRequest.Login.class),
                    @io.swagger.v3.oas.annotations.media.DiscriminatorMapping(value = "refresh", schema = IssuanceTokenRequest.Refresh.class)
            }
    )
    @Getter
    @Setter
    public static abstract class Base {
        @Schema(description = "토큰 요청 타입")
        @JsonProperty("grant_type")
        private WAIMAuthGrantType grantType;
    }

    @Schema(description = "로그인 방식 요청")
    @Getter
    @Setter
    public static class Login extends Base {
        @Schema(description = "User ID" , examples = "admin")
        @JsonProperty("id")
        private String id;

        @Schema(description = "User Password" , examples = "ww1111")
        @JsonProperty("password")
        private String password;
    }

    @Schema(description = "재발급 요청")
    @Getter
    @Setter
    public static class Refresh extends Base {

    }

}
