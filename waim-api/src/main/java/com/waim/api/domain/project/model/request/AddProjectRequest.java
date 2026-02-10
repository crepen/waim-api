package com.waim.api.domain.project.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddProjectRequest {
    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("project_alias")
    private String projectAlias;
}
