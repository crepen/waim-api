package com.waim.module.domain.task.service;


import com.waim.module.domain.task.specificity.TaskSpecificity;
import com.waim.module.storage.domain.task.entity.TaskEntity;
import com.waim.module.storage.domain.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    @Transactional
    public List<TaskEntity> getActiveTask(){
        return taskRepository.findAll(TaskSpecificity.findLoopTaskActiveItem());
    }


    @Transactional
    public List<TaskEntity> getLoopActiveTask(){
        return taskRepository.findAll(TaskSpecificity.findLoopTaskActiveItem());
    }

}
