package com.waim.module.core.domain.project.model.entity.id;


import com.waim.module.data.domain.project.ProjectRole;

import java.io.Serializable;

public record ProjectRoleId (
        String project,
        String user,
        ProjectRole role
) implements Serializable {}
