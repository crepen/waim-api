package com.waim.api.domain.project.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsertProjectJobRequest {

    @JsonProperty("task_type")
    private String taskType;

    @JsonProperty("interval_delay")
    private String intervalDelay;

    @JsonProperty("task_status")
    private String taskStatus;

    @JsonProperty("attributes")
    private Map<String, String> attributes;
}
