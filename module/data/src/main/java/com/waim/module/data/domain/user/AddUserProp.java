package com.waim.module.data.domain.user;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddUserProp {
    private String id;
    private String name;
    private String email;
    private String password;
    private UserRole role;
    private UserStatus status = UserStatus.ACTIVE;
}
