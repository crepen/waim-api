package com.waim.api.domain.user_config.model.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetUserConfigRequest {
    private String key;
    private String value;
}
