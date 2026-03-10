package com.waim.module.data.domain.project;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectData {
    String uid;

    @JsonProperty("project_name")
    String projectName;

    @JsonProperty("project_alias")
    String projectAlias;

    @JsonProperty("create_timestamp")
    Long createTimestamp;

    @JsonProperty("update_timestamp")
    Long updateTimestamp;

    @JsonProperty("project_owner_name")
    String projectOwnerName;

    @JsonProperty("project_owner_uid")
    String projectOwnerUid;

    @JsonProperty("group_uid")
    String groupUid;
}
