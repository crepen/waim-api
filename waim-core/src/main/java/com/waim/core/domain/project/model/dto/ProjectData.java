package com.waim.core.domain.project.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectData {
    String uid;

    @JsonProperty("project_name")
    String projectName;

    @JsonProperty("project_alias")
    String projectAlias;

    @JsonProperty("create_timestamp")
    long createTimestamp;

    @JsonProperty("update_timestamp")
    long updateTimestamp;

    @JsonProperty("project_owner_name")
    String projectOwnerName;

    @JsonProperty("project_owner_uid")
    String projectOwnerUid;
}
