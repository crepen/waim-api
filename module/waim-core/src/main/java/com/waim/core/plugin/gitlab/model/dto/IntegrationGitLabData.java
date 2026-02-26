package com.waim.core.plugin.gitlab.model.dto;

import com.waim.core.plugin.gitlab.model.dto.obj.GitLabCommit;
import com.waim.core.plugin.gitlab.model.dto.obj.GitLabPipeline;
import lombok.*;

@Getter
@Setter
@Builder
public class IntegrationGitLabData {
    private GitLabPipeline lastPipeline;
    private GitLabCommit lastCommit;
}
