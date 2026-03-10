package com.waim.module.data.domain.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupPermissionData {
    private String uid;

    @JsonProperty("group_uid")
    private String groupUid;

    @JsonProperty("user_uid")
    private String userUid;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("user_name")
    private String userName;

    private String role;
}
