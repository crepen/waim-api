package com.waim.api.domain.group.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddGroupRequest {
    @JsonProperty("group_name")
    private String groupName;

    @JsonProperty("group_alias")
    private String groupAlias;

    @JsonProperty("parent_group_uid")
    private String parentGroupUid;
}
