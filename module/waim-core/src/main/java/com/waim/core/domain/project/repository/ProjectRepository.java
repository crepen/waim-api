package com.waim.core.domain.project.repository;

import com.waim.core.domain.project.model.entity.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity , String> , JpaSpecificationExecutor<ProjectEntity> {
    Optional<ProjectEntity> findByUid(String uid);
}
