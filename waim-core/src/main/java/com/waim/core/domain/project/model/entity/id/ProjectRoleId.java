package com.waim.core.domain.project.model.entity.id;

import com.waim.core.domain.project.model.dto.enumable.ProjectRole;

import java.io.Serializable;


public record ProjectRoleId (
        String project,
        String user,
        ProjectRole role
) implements Serializable {}
