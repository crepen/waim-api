package com.waim.module.storage.domain.project.repository;

import com.waim.module.storage.domain.project.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity , String> , JpaSpecificationExecutor<ProjectEntity> {

}
