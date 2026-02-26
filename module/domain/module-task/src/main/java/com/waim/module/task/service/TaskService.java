package com.waim.module.task.service;


import com.waim.module.task.repository.TaskRepository;

public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    public void getTask(){

    }
}
