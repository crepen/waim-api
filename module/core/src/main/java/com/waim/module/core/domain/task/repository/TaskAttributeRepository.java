package com.waim.module.core.domain.task.repository;

import com.waim.module.core.domain.task.model.entity.TaskAttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskAttributeRepository extends JpaRepository<TaskAttributeEntity, Long> {
    List<TaskAttributeEntity> findAllByTaskUid(String taskUid);
    void deleteAllByTaskUid(String taskUid);
}
