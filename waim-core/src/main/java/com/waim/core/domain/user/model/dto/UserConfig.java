package com.waim.core.domain.user.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserConfig {
    private String key;
    private String value;
}
