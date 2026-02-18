package com.waim.core.domain.project.service;

import com.waim.core.common.util.crypto.CryptoProvider;
import com.waim.core.domain.project.model.entity.ProjectConfigEntity;
import com.waim.core.domain.project.model.entity.ProjectEntity;
import com.waim.core.domain.project.model.error.ProjectNotFoundException;
import com.waim.core.domain.project.repository.ProjectConfigRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectConfigService {

    private final ProjectConfigRepository projectConfigRepository;
    private final ProjectService projectService;
    private final CryptoProvider cryptoProvider;

    public List<ProjectConfigEntity> getAllConfig(String projectUid){

        Specification<ProjectConfigEntity> spec = ((root, query, cb) -> cb.conjunction());

        spec = spec.and((root, query, cb) -> cb.equal(root.get("project").get("uid"), projectUid));

        return projectConfigRepository.findAll(spec);
    }

    public List<ProjectConfigEntity> getConfigs(String projectUid , String... configKeys){

        if(configKeys.length == 0){
            return new ArrayList<>();
        }

        Specification<ProjectConfigEntity> spec = (root, query, cb) -> {
            Predicate projectPredicate = cb.equal(root.get("project").get("uid"), projectUid);

            CriteriaBuilder.In<String> inClause = cb.in(root.get("configKey"));
            for (String key : configKeys) {
                inClause.value(key); // 각 키를 개별적으로 추가
            }

            return cb.and(projectPredicate, inClause);
        };


        return projectConfigRepository.findAll(spec);
    }

    public Optional<ProjectConfigEntity> getConfig(String projectUid , String configKey){
        if(configKey == null){
            return Optional.empty();
        }
        var findDataList = this.getConfigs(projectUid , configKey);

        if(findDataList.isEmpty()){
            return Optional.empty();
        }
        else{
            return Optional.of(findDataList.getFirst());
        }
    }

    public void setConfigs(String projectAlias , String userUid , Map<String , String> configItem){
        Optional<ProjectEntity> projectEntity = projectService.getActiveProjectUsingAliasAndOwnerUid(
                projectAlias, userUid
        );
        setConfigs(projectEntity , configItem);
    }

    public void setConfigs(String projectUid , Map<String , String> configItem){
        Optional<ProjectEntity> projectEntity = projectService.getActiveProject(projectUid);
        setConfigs(projectEntity, configItem);
    }

    public void setConfigs(Optional<ProjectEntity> project, Map<String , String> configItem) {

        if (project.isEmpty()) {
            throw new ProjectNotFoundException();
        }

        List<ProjectConfigEntity> configs = configItem.entrySet().stream()
                .map(entry ->
                        ProjectConfigEntity.builder()
                                .project(project.get())        // 연관된 프로젝트 엔티티 설정
                                .configKey(entry.getKey())
                                .configValue(entry.getValue())
                                .build()
                )
                .toList();

        projectConfigRepository.saveAll(configs);
    }
}
