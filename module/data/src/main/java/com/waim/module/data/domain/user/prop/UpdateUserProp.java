package com.waim.module.data.domain.user.prop;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
public class UpdateUserProp {
    private String userUid;
    private String password;
    private String email;
    private String name;
    private String role;
    private Map<String , String> config;
}
