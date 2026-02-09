package com.waim.core.domain.user.model.dto;

import com.waim.core.domain.user.model.UserState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddUserDTO {
    private String userName;
    private String userId;
    private String password;
    private String email;
    private UserState userState;
}
