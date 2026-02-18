package com.waim.core.plugin.gitlab.service;

import com.waim.core.common.model.dto.ConfigItem;
import com.waim.core.common.model.error.PlatformUnknownException;
import com.waim.core.domain.project.model.entity.ProjectConfigEntity;
import com.waim.core.domain.project.model.entity.ProjectEntity;
import com.waim.core.domain.project.model.error.ProjectNotFoundException;
import com.waim.core.domain.project.service.ProjectConfigService;
import com.waim.core.domain.project.service.ProjectService;
import com.waim.core.plugin.gitlab.model.dto.IntegrationGitLabData;
import com.waim.core.plugin.gitlab.model.dto.obj.GitLabCommit;
import com.waim.core.plugin.gitlab.model.dto.obj.GitLabPipeline;
import com.waim.core.plugin.gitlab.model.dto.obj.GitLabProject;
import com.waim.core.plugin.gitlab.model.enumable.GitLabValidateError;
import com.waim.core.plugin.gitlab.model.error.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


@Service
@Slf4j
public class GitLabPluginService {

    private final GitLabApiService gitLabApiService;
    private final ProjectConfigService projectConfigService;
    private final ProjectService projectService;

    public GitLabPluginService(
            GitLabApiService gitLabApiService,
            ProjectConfigService projectConfigService,
            ProjectService projectService
    ) {
        this.gitLabApiService = gitLabApiService;
        this.projectConfigService = projectConfigService;
        this.projectService = projectService;
    }

    public IntegrationGitLabData getIntegrationGitLabDataByProjectAlias (String userUid , String projectAlias) {
        Optional<ProjectEntity> project = projectService.getActiveProjectUsingAliasAndOwnerUid(projectAlias, userUid);

        if (project.isEmpty()) {
            throw new ProjectNotFoundException();
        }

        return getIntegrationGitLabData(project.get().getUid());
    }

    public IntegrationGitLabData getIntegrationGitLabData(String projectUid) {

        Map<String, String> configMap = projectConfigService.getConfigs(
                        projectUid,
                        "PLUGIN_INTEGRATION_GITLAB_URL",
                        "PLUGIN_INTEGRATION_GITLAB_PROJECT_ID",
                        "PLUGIN_INTEGRATION_GITLAB_TOKEN"
                ).stream()
                .collect(Collectors.toMap(ProjectConfigEntity::getConfigKey, ProjectConfigEntity::getConfigValue));

        if (configMap.size() < 3) {
            throw new GitLabPluginConfigureUndefinedException();
        }

        String baseUrl = configMap.get("PLUGIN_INTEGRATION_GITLAB_URL");
        String token = configMap.get("PLUGIN_INTEGRATION_GITLAB_TOKEN");
        Integer projectId = Integer.parseInt(configMap.get("PLUGIN_INTEGRATION_GITLAB_PROJECT_ID"));


        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

            Future<GitLabPipeline> pipelineFuture = executor.submit(() -> {
                try {
                    return gitLabApiService.getLastPipeline(baseUrl, projectId, token);
                } catch (Exception e) {
                    return null;
                }
            });

            Future<GitLabCommit> commitFuture = executor.submit(() -> {
                try {
                    return gitLabApiService.getProjectLastCommit(baseUrl, projectId, token).orElse(null);
                } catch (Exception e) {
                    return null;
                }
            });


            GitLabPipeline pipeline = pipelineFuture.get();
            GitLabCommit commit = commitFuture.get();




            return IntegrationGitLabData.builder()
                    .lastPipeline(pipeline)
                    .lastCommit(commit)
                    .build();

        } catch (Exception ex) {
            throw new PlatformUnknownException(ex);
        }


    }

    public List<ProjectConfigEntity> getGitLabPluginProjectConfigUseProjectAlias(
            String userUid , String projectAlias , boolean maskingSecureValue
    ) {
        Optional<ProjectEntity> projectEntity = projectService.getActiveProjectUsingAliasAndOwnerUid(projectAlias , userUid);
        return getGitLabPluginProjectConfig(projectEntity , maskingSecureValue);
    }

    public List<ProjectConfigEntity> getGitLabPluginProjectConfigUseProjectUid(
            String userUid , String projectUid , boolean maskingSecureValue
    ){
        Optional<ProjectEntity> projectEntity = projectService.getActiveProject(userUid, projectUid);
        return getGitLabPluginProjectConfig(projectEntity , maskingSecureValue);
    }

    public List<ProjectConfigEntity> getGitLabPluginProjectConfig(
            Optional<ProjectEntity> project, boolean maskingSecureValue
    ){
        if(project.isEmpty()){
            throw new ProjectNotFoundException();
        }

        return projectConfigService.getConfigs(
                project.get().getUid(),
                maskingSecureValue,
                "PLUGIN_INTEGRATION_GITLAB_URL",
                "PLUGIN_INTEGRATION_GITLAB_PROJECT_ID",
                "PLUGIN_INTEGRATION_GITLAB_TOKEN"
        );
    }


    public void setGitLabPluginProjectConfig(
            String baseUrl,
            Integer projectId,
            String token,
            String userUid,
            String projectAlias
    ){
        if(!StringUtils.hasText(baseUrl)){
            throw new GitLabValidateException(GitLabValidateError.GITLAB_BASE_URL);
        }

        if(projectId == null){
            throw new GitLabValidateException(GitLabValidateError.PROJECT_ID);
        }

        if(!StringUtils.hasText(token)){
            throw new GitLabValidateException(GitLabValidateError.GITLAB_PROJECT_TOKEN);
        }


        List<ConfigItem> configItems = new ArrayList<>();

        configItems.add(new ConfigItem("PLUGIN_INTEGRATION_GITLAB_URL", baseUrl , false));
        configItems.add(new ConfigItem("PLUGIN_INTEGRATION_GITLAB_PROJECT_ID", String.valueOf(projectId) , false));
        configItems.add(new ConfigItem("PLUGIN_INTEGRATION_GITLAB_TOKEN", token , true));



        try{
            List<GitLabProject> tokenProjects = gitLabApiService.getTokenProjects(
                    baseUrl,
                    token
            );

            var isProjectExist = tokenProjects.stream().anyMatch(x-> Objects.equals(x.getId(), projectId));

            if(!isProjectExist){
                throw new GitLabProjectNotFoundException();
            }

            projectConfigService.setConfigsUserProjectAlias(projectAlias , userUid , configItems);
        }
        catch (GitLabPluginException gpex){
            throw gpex;
        }
        catch (Exception ex){
            throw new GitLabConnException();
        }




    }



}
