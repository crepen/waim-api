package com.waim.module.storage.domain.project.repository;

import com.waim.module.storage.domain.project.entity.ProjectConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectConfigRepository extends JpaRepository<ProjectConfigEntity , String> , JpaSpecificationExecutor<ProjectConfigEntity> {

}
