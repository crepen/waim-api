package com.waim.core.common.util.jwt.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtUserDetail {
    private String userUid;
    private String userName;
    private List<String> userRole;
}
