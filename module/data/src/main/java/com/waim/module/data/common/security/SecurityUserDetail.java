package com.waim.module.data.common.security;

import com.waim.module.data.domain.user.UserRole;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUserDetail {
    private String id;
    private String userName;
    private String email;
    private String uniqueId;
    private List<String> roles;
}
