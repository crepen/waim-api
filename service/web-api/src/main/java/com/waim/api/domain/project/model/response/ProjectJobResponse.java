package com.waim.api.domain.project.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectJobResponse {
    @JsonProperty("uid")
    private String uid;

    @JsonProperty("project_uid")
    private String projectUid;

    @JsonProperty("owner_uid")
    private String ownerUid;

    @JsonProperty("task_type")
    private String taskType;

    @JsonProperty("task_status")
    private String taskStatus;

    @JsonProperty("interval_delay")
    private String intervalDelay;

    @JsonProperty("next_run_timestamp")
    private Long nextRunTimestamp;

    @JsonProperty("attributes")
    private Map<String, String> attributes;

    @JsonProperty("create_timestamp")
    private Long createTimestamp;

    @JsonProperty("update_timestamp")
    private Long updateTimestamp;
}
