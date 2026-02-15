package com.waim.core.domain.project.repository;

import com.waim.core.domain.project.model.entity.ProjectRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRoleRepository extends JpaRepository<ProjectRoleEntity , String> , JpaSpecificationExecutor<ProjectRoleEntity> {
}
