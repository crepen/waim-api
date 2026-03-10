package com.waim.api.domain.user.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserResponse {
    private String uid;
    private String userId;
    private String userName;
    private String email;
    private String role;
    private String status;
}
