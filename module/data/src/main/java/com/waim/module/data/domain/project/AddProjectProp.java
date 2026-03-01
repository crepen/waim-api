package com.waim.module.data.domain.project;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProjectProp {
    private String projectName;
    private String projectAlias;
    private String projectOwnerUserUid;
}
