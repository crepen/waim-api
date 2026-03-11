package com.waim.module.core.domain.task.repository;

import com.waim.module.core.domain.task.model.entity.TaskRunLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRunLogRepository extends JpaRepository<TaskRunLogEntity, Long>, JpaSpecificationExecutor<TaskRunLogEntity> {
}
