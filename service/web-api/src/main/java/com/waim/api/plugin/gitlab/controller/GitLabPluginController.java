package com.waim.api.plugin.gitlab.controller;

import com.waim.api.common.model.response.BaseResponse;
import com.waim.api.plugin.gitlab.model.request.GitLabTestConnectRequest;
import com.waim.api.plugin.gitlab.model.request.UpdateGitLabConfigRequest;
import com.waim.api.plugin.gitlab.model.response.GitLabIntegrationDataResponse;
import com.waim.core.common.model.dto.ConfigItem;
import com.waim.core.common.util.jwt.model.JwtUserDetail;
import com.waim.core.domain.project.model.entity.ProjectConfigEntity;
import com.waim.core.plugin.gitlab.model.dto.obj.GitLabProject;
import com.waim.core.plugin.gitlab.model.error.GitLabProjectNotFoundException;
import com.waim.core.plugin.gitlab.service.GitLabApiService;
import com.waim.core.plugin.gitlab.service.GitLabPluginService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/plugin/gitlab")
@Tag(name = "PLUGIN_GITLAB" , description = "GitLab Integration API")
public class GitLabPluginController {

    private final GitLabApiService gitLabApiService;
    private final GitLabPluginService gitLabPluginService;

    /**
     * <h3>GitLab Project 연결 체크</h3>
     */
    @PostMapping("connect")
    public ResponseEntity<?> testConnect(
            @RequestBody GitLabTestConnectRequest reqBody
    ) {
        List<GitLabProject> projects = gitLabApiService.getTokenProjects(
                reqBody.getBaseUrl(),
                reqBody.getGitlabToken()
        );

        boolean isProjectExist = projects.stream().anyMatch(p -> p.getId().equals(reqBody.getProjectId()));

        if (!isProjectExist) {
            throw new GitLabProjectNotFoundException();
        }

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .build()
        );
    }


    /**
     * <h3>메인 프로젝트와 연결된 GitLab Project 정보 조회</h3>
     *
     * <ul>
     *     <li>Last Commit Data</li>
     *     <li>Last Pipeline Data</li>
     * </ul>
     */
    @GetMapping("/itg/{projectAlias}")
    public ResponseEntity<?> getIntegrationGitLabData(
            @AuthenticationPrincipal JwtUserDetail userDetail,
            @PathVariable String projectAlias
    ) {
        var data = gitLabPluginService.getIntegrationGitLabDataByProjectAlias(
                userDetail.getUserUid(),
                projectAlias
        );

        GitLabIntegrationDataResponse response = null;

        if (data != null) {
            response = GitLabIntegrationDataResponse.builder().build();

            if (data.getLastPipeline() != null) {
                response.setLastPipeline(
                        GitLabIntegrationDataResponse.Pipeline.builder()
                                .state(data.getLastPipeline().getStatus())
                                .createdAt(data.getLastPipeline().getCreatedAt().toInstant().toEpochMilli())
                                .build()
                );
            }

            if (data.getLastCommit() != null) {

                GitLabIntegrationDataResponse.Commit resCommit;

                resCommit = GitLabIntegrationDataResponse.Commit.builder()
                        .id(data.getLastCommit().getId())
                        .message(data.getLastCommit().getMessage())
                        .createdAt(data.getLastCommit().getCreatedAt().toInstant().toEpochMilli())
                        .webUrl(data.getLastCommit().getWebUrl())
                        .build();

                response.setLastCommit(resCommit);
            }
        }

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .result(response)
                        .build()
        );
    }


    /**
     * <h3>메인 프로젝트 - Gitlab plugin 설정 등록</h3>
     *
     * <ul>
     *     <li>GitLab Base URL</li>
     *     <li>GitLab Project ID</li>
     *     <li>GitLab Project Access Token</li>
     * </ul>
     */
    @PostMapping("/itg/{projectAlias}/config")
    public ResponseEntity<?> setGitLabConfig(
            @AuthenticationPrincipal JwtUserDetail userDetail,
            @PathVariable String projectAlias,
            @RequestBody UpdateGitLabConfigRequest reqBody
    ) {
        gitLabPluginService.setGitLabPluginProjectConfig(
                reqBody.getBaseUrl(),
                reqBody.getProjectId(),
                reqBody.getGitlabToken(),
                userDetail.getUserUid(),
                projectAlias
        );

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .build()
        );

    }


    /**
     * <h3>메인 프로젝트 - Gitlab plugin 설정 조회</h3>
     */
    @GetMapping("/itg/{projectAlias}/config")
    public ResponseEntity<?> getGitLabConfig(
            @AuthenticationPrincipal JwtUserDetail userDetail,
            @PathVariable String projectAlias
    ) {

        List<ProjectConfigEntity> config = gitLabPluginService.getGitLabPluginProjectConfigUseProjectAlias(
                userDetail.getUserUid(),
                projectAlias,
                true
        );

        return ResponseEntity.ok(
                BaseResponse.Success.builder()
                        .result(
                                config.stream()
                                        .map(ProjectConfigEntity::castConfigItem)
                                        .toList()
                        )
                        .build()
        );

    }

}
