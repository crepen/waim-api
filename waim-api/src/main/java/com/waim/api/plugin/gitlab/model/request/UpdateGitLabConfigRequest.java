package com.waim.api.plugin.gitlab.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateGitLabConfigRequest {

    @JsonProperty("base_url")
    private String baseUrl;

    @JsonProperty("project_id")
    private int projectId;

    @JsonProperty("gitlab_token")
    private String gitlabToken;

}
