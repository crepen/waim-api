package com.waim.core.domain.project.service;

import com.waim.core.common.model.error.WAIMException;
import com.waim.core.domain.project.model.entity.ProjectEntity;
import com.waim.core.domain.project.model.error.ProjectErrorCode;
import com.waim.core.domain.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;


    public void addProject(
            String projectName,
            String projectAlias,
            String createUserUid
    ){
        if(!StringUtils.hasText(projectName)) {
            throw new WAIMException(
                    ProjectErrorCode.PROJECT_NAME_EMPTY
            );
        }
//        else if(projectName.matches("")){
//            // TODO : Project Name Length Check
//        }

        if(!StringUtils.hasText(projectName)){
            throw new WAIMException(
                    ProjectErrorCode.PROJECT_ALIAS_EMPTY
            );
        }
        else if (!projectAlias.matches("^[a-z0-9]*$")) {
            throw new WAIMException(
                    ProjectErrorCode.PROJECT_ALIAS_NOT_ALLOW
            );
        }

        projectRepository.save(
                ProjectEntity.builder()
                        .projectName(projectName)
                        .alias(projectAlias)
                        .projectOwnerUid(createUserUid)
                        .build()
        );
    }
}
