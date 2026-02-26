package com.waim.module.storage.domain.task.repository;

import com.waim.module.storage.domain.task.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity , String> , JpaSpecificationExecutor<TaskEntity> {

}
