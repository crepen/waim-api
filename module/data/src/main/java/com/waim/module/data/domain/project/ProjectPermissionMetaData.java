package com.waim.module.data.domain.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPermissionMetaData {
    private String role;

    @JsonProperty("display_name")
    private String displayName;

    private String description;
}
