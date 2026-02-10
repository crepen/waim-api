package com.waim.core.domain.project.service;

import com.waim.core.common.model.error.WAIMException;
import com.waim.core.domain.project.model.dto.ProjectData;
import com.waim.core.domain.project.model.entity.ProjectEntity;
import com.waim.core.domain.project.model.error.ProjectErrorCode;
import com.waim.core.domain.project.repository.ProjectRepository;
import com.waim.core.domain.project.repository.spec.ProjectSpecification;
import com.waim.core.domain.user.model.entity.UserEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final EntityManager entityManager;

    /**
     * <h3>사용자 프로젝트 생성</h3>
     *
     * <p>
     *
     * </p>
     *
     * @param projectName   프로젝트명
     * @param projectAlias  프로젝트 약칭
     * @param createUserUid 프로젝트 생성자 UID
     */
    public void addProject(
            String projectName,
            String projectAlias,
            String createUserUid
    ) {
        if (!StringUtils.hasText(projectName)) {
            throw new WAIMException(ProjectErrorCode.PROJECT_NAME_EMPTY);
        } else if (projectRepository.exists(ProjectSpecification.existsUserProjectName(projectName, createUserUid))) {
            throw new WAIMException(ProjectErrorCode.PROJECT_NAME_DUPLICATE);
        }
//        else if(projectName.matches("")){
//            // TODO : Project Name Length Check
//        }

        if (!StringUtils.hasText(projectAlias)) {
            throw new WAIMException(ProjectErrorCode.PROJECT_ALIAS_EMPTY);
        } else if (!projectAlias.matches("^[a-z0-9-]*$")) {
            throw new WAIMException(ProjectErrorCode.PROJECT_ALIAS_NOT_ALLOW);
        } else if (projectRepository.exists(ProjectSpecification.existsUserProjectAlias(projectAlias, createUserUid))) {
            throw new WAIMException(ProjectErrorCode.PROJECT_ALIAS_DUPLICATE);
        }

        projectRepository.save(
                ProjectEntity.builder()
                        .projectName(projectName)
                        .projectAlias(projectAlias)
                        .projectOwner(
                                entityManager.getReference(
                                        UserEntity.class,
                                        createUserUid
                                )
                        )
                        .build()
        );
    }


    /**
     * <h3>프로젝트 정보 조회</h3>
     * <p>
     * 프로젝트 UID로 검색
     * </p>
     *
     * @param uid Project UID
     */
    public Optional<ProjectData> getProject(String uid) {

        // TODO : Validation check

        var searchProject = projectRepository.findByUid(uid);

        return searchProject.map(
                projectEntity ->
                        ProjectData.builder()
                                .uid(projectEntity.getUid())
                                .projectName(projectEntity.getProjectName())
                                .projectAlias(projectEntity.getProjectAlias())
                                .createTimestamp(
                                        projectEntity.getCreateAt()
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant().toEpochMilli()
                                )
                                .updateTimestamp(
                                        projectEntity.getUpdateAt()
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant().toEpochMilli()
                                )
                                .build()
        );
    }

    /**
     * <h3>프로젝트 정보 조회</h3>
     * <p>
     * 프로젝트 UID로 검색
     * </p>
     *
     * @param userId       Owner user id
     * @param projectAlias Project Alias
     */
    public Optional<ProjectData> getProject(String userId, String projectAlias) {

        // TODO : Validation check

        var searchProject = projectRepository.findOne(ProjectSpecification.searchUserProjectAlias(userId, projectAlias));

        return searchProject.map(
                projectEntity ->
                        ProjectData.builder()
                                .uid(projectEntity.getUid())
                                .projectName(projectEntity.getProjectName())
                                .projectAlias(projectEntity.getProjectAlias())
                                .createTimestamp(
                                        projectEntity.getCreateAt()
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant().toEpochMilli()
                                )
                                .updateTimestamp(
                                        projectEntity.getUpdateAt()
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant().toEpochMilli()
                                )
                                .build()
        );
    }

}
