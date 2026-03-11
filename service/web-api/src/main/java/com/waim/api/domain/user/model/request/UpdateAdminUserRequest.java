package com.waim.api.domain.user.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAdminUserRequest {
    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_email")
    private String email;

    @JsonProperty("user_password")
    private String password;

    @JsonProperty("user_role")
    private String role;
}
