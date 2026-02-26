package com.waim.core.plugin.gitlab.service;


import com.waim.core.plugin.gitlab.model.dto.obj.GitLabCommit;
import com.waim.core.plugin.gitlab.model.dto.GitLabConnectOption;
import com.waim.core.plugin.gitlab.model.dto.obj.GitLabPipeline;
import com.waim.core.plugin.gitlab.model.dto.obj.GitLabProject;
import com.waim.core.plugin.gitlab.model.error.GitLabUnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GitLabApiService {


    public GitLabPipeline getLastPipeline(String baseUrl, Integer projectId, String token) {
        if(!StringUtils.hasText(token)) {
            throw new GitLabUnauthorizedException();
        }

        GitLabConnector conn = new GitLabConnector(baseUrl);

        return conn.getData(
                String.format("/api/v4/projects/%s/pipelines/latest", projectId),
                GitLabConnectOption.builder()
                        .token(token)
                        .build(),
                GitLabPipeline.class
        );
    }

    public List<GitLabProject> getTokenProjects(String baseUrl, String token) {

        if(!StringUtils.hasText(token)) {
            throw new GitLabUnauthorizedException();
        }

        GitLabConnector conn = new GitLabConnector(baseUrl);

        return conn.getDataList(
                "/api/v4/projects",
                GitLabConnectOption.builder()
                        .token(token)
                        .build(),
                GitLabProject.class
        );


    }

    public List<GitLabCommit> getProjectCommits(String baseUrl, Integer projectId, String token) {

        if(!StringUtils.hasText(token)) {
            throw new GitLabUnauthorizedException();
        }

        GitLabConnector conn = new GitLabConnector(baseUrl);

        return conn.getDataList(
                String.format("/api/v4/projects/%s/repository/commits", projectId),
                GitLabConnectOption.builder()
                        .token(token)
                        .build(),
                GitLabCommit.class
        );
    }

    public Optional<GitLabCommit> getProjectLastCommit(String baseUrl, Integer projectId, String token) {
        if(!StringUtils.hasText(token)) {
            throw new GitLabUnauthorizedException();
        }

        GitLabConnector conn = new GitLabConnector(baseUrl);

        List<GitLabCommit> commits = conn.getDataList(
                String.format("/api/v4/projects/%s/repository/commits?per_page=1", projectId),
                GitLabConnectOption.builder()
                        .token(token)
                        .build(),
                GitLabCommit.class
        );

        return !commits.isEmpty()
                ? Optional.of(commits.getFirst())
                : Optional.empty();
    }
}
