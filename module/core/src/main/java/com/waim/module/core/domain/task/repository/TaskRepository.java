package com.waim.module.core.domain.task.repository;

import com.waim.module.core.domain.task.model.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, String> , JpaSpecificationExecutor<TaskEntity> {

}
