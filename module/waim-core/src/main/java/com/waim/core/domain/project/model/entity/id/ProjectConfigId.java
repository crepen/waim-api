package com.waim.core.domain.project.model.entity.id;

import java.io.Serializable;

public record ProjectConfigId(
        String project,
        String configKey
) implements Serializable {}
