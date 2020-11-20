package com.hwhhhh.taskmanager.service;

import com.hwhhhh.taskmanager.dto.TaskDto;
import com.hwhhhh.taskmanager.util.HttpCallBack;

import java.util.List;

public interface TaskService {
    String GET_ALLTASK = "task/all";        //获取所有任务的url
    String POST_TASK = "task";
    String DELETE_TASK = "task/";
    List<TaskDto> getAllTask(HttpCallBack httpCallBack);
    void createTask(TaskDto taskDto);
    void deleteTask(int id);
}
