package com.waim.module.data.domain.group;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddGroupProp {
    private String groupName;
    private String groupAlias;
    private String parentGroupUid;
    private String actionUserUid;
}
