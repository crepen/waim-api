package com.waim.module.core.domain.project.repository;

import com.waim.module.core.domain.project.model.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, String> , JpaSpecificationExecutor<ProjectEntity> {

}
