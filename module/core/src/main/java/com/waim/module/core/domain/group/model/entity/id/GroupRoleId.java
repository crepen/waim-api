package com.waim.module.core.domain.group.model.entity.id;

import com.waim.module.data.domain.group.GroupRole;

import java.io.Serializable;

public record GroupRoleId(
        String group,
        String user,
        GroupRole role
) implements Serializable {}
