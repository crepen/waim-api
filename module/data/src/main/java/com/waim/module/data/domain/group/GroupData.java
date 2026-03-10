package com.waim.module.data.domain.group;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupData {
    private String uid;

    @JsonProperty("group_name")
    private String groupName;

    @JsonProperty("group_alias")
    private String groupAlias;

    @JsonProperty("parent_group_uid")
    private String parentGroupUid;

    @JsonProperty("child_group_count")
    private Long childGroupCount;

    @JsonProperty("linked_project_count")
    private Long linkedProjectCount;
}
