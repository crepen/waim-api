package com.waim.module.data.domain.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPermissionData {
    private String uid;

    @JsonProperty("project_uid")
    private String projectUid;

    @JsonProperty("user_uid")
    private String userUid;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("user_name")
    private String userName;

    private String role;
}
