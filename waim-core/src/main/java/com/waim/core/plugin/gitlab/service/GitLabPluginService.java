package com.waim.core.plugin.gitlab.service;

import com.waim.core.plugin.gitlab.model.dto.GitLabCommit;
import com.waim.core.plugin.gitlab.model.dto.GitLabConnectOption;
import com.waim.core.plugin.gitlab.model.dto.response.GitLabApiProjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class GitLabPluginService {


    public void getProjectUsingToken(String baseUrl, String token) {
        GitLabConnector conn = new GitLabConnector(baseUrl);

        var ss = conn.getData(
                "/api/v4/projects",
                GitLabConnectOption.builder()
                        .token(token)
                        .build(),
                GitLabApiProjectResponse.TokenMembership.class
        );


    }

    public List<GitLabCommit> getProjectCommits(String baseUrl, String projectId, String token) {

        GitLabConnector conn = new GitLabConnector(baseUrl);
        var apiRes = conn.getDataList(
                String.format("/api/v4/projects/%s/repository/commits", projectId),
                GitLabConnectOption.builder()
                        .token(token)
                        .build(),
                GitLabCommit.class
        );

        return apiRes.getData();
    }
}
