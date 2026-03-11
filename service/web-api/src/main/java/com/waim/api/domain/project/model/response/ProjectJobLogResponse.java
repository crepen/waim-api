package com.waim.api.domain.project.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectJobLogResponse {
    @JsonProperty("idx")
    private Long idx;

    @JsonProperty("project_uid")
    private String projectUid;

    @JsonProperty("task_uid")
    private String taskUid;

    @JsonProperty("task_type")
    private String taskType;

    @JsonProperty("run_status")
    private String runStatus;

    @JsonProperty("response_status")
    private Integer responseStatus;

    @JsonProperty("duration_ms")
    private Long durationMs;

    @JsonProperty("message")
    private String message;

    @JsonProperty("create_timestamp")
    private Long createTimestamp;
}
