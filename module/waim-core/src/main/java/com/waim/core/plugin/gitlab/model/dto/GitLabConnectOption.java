package com.waim.core.plugin.gitlab.model.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
public record GitLabConnectOption(
        String token
) {}
