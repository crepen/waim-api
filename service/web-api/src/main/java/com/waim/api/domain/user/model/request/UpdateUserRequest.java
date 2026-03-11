package com.waim.api.domain.user.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {

    private String name;
    private String password;
    private String email;
    private String role;
    private Map<String , String> config;
}
