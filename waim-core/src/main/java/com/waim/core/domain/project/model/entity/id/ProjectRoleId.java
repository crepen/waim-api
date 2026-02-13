package com.waim.core.domain.project.model.entity.id;

import java.io.Serializable;

public record ProjectRoleId (
        String project,
        String user
) implements Serializable {}
