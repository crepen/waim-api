package com.waim.api.domain.user_config.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserConfigResponse {
    private String key;
    private String value;
}
