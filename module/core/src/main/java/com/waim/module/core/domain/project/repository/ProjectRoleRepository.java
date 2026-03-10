package com.waim.module.core.domain.project.repository;

import com.waim.module.core.domain.project.model.entity.ProjectRoleEntity;
import com.waim.module.core.domain.project.model.entity.id.ProjectRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRoleRepository extends JpaRepository<ProjectRoleEntity, ProjectRoleId> {
    List<ProjectRoleEntity> findByProject_Uid(String projectUid);

    List<ProjectRoleEntity> findByProject_UidAndUser_Uid(String projectUid, String userUid);

    void deleteByProject_UidAndUser_Uid(String projectUid, String userUid);
}
