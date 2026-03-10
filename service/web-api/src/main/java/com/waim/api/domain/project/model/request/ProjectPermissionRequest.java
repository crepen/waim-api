package com.waim.api.domain.project.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPermissionRequest {
    @JsonProperty("user_uid")
    private String userUid;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("role")
    private String role;
}
