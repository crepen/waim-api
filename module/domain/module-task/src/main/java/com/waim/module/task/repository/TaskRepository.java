package com.waim.module.task.repository;

import com.waim.module.task.model.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends JpaRepository<TaskEntity , String> , JpaSpecificationExecutor<TaskEntity> {

}
