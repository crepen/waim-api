package com.waim.core.domain.project.service;

import com.waim.core.common.model.error.WAIMException;
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
     * @param projectName 프로젝트명
     * @param projectAlias 프로젝트 약칭
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
}
