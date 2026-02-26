package com.waim.core.plugin.gitlab.model.dto.obj;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class GitLabPipeline {
    private Integer id;

    @JsonProperty("project_id")
    private Integer projectId;

    @JsonProperty("created_at")
    private OffsetDateTime createdAt;

    @JsonProperty("updated_at")
    private OffsetDateTime updatedAt;

    @JsonProperty("status")
    private String status;
}
