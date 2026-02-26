package com.waim.core.plugin.gitlab.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class GitLabApiProjectResponse {
    @Getter
    @Setter
    @Builder
    public static class TokenMembership{
        private String id;
        private String name;
    }
}
