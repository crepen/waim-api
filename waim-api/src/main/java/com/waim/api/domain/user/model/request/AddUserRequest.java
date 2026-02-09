package com.waim.api.domain.user.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddUserRequest {
    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("user_password")
    private String password;

    @JsonProperty("user_email")
    private String email;

}
