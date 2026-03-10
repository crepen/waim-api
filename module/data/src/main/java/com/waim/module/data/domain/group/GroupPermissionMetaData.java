package com.waim.module.data.domain.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupPermissionMetaData {
    private String role;

    @JsonProperty("display_name")
    private String displayName;

    private String description;
}
