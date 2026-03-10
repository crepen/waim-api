package com.waim.module.data.domain.group;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupProp {
    private String groupUid;
    private String groupName;
    private String groupAlias;
    private String parentGroupUid;
}
