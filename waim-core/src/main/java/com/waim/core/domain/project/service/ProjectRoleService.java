package com.waim.core.domain.project.service;

import com.waim.core.common.model.error.WAIMException;
import com.waim.core.domain.project.model.dto.enumable.ProjectRole;
import com.waim.core.domain.project.model.entity.ProjectEntity;
import com.waim.core.domain.project.model.entity.ProjectRoleEntity;
import com.waim.core.domain.project.repository.ProjectRoleRepository;
import com.waim.core.domain.user.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectRoleService {

    private final ProjectRoleRepository projectRoleRepository;

    public void addRole(Optional<ProjectEntity> project, Optional<UserEntity> user , ProjectRole... roles) {
        if(project.isEmpty()) {
            /// TODO : Project Empty Exception
            throw new WAIMException();
        }

        if(user.isEmpty()) {
            // TODO : User Empty Exception
            throw new WAIMException();
        }


        List<ProjectRoleEntity> addRoles = new ArrayList<>();

        for(ProjectRole role : roles){
            addRoles.add(
                    ProjectRoleEntity.builder()
                            .project(project.get())
                            .user(user.get())
                            .role(role)
                            .build()
            );
        }

        projectRoleRepository.saveAll(addRoles);
    }



}
