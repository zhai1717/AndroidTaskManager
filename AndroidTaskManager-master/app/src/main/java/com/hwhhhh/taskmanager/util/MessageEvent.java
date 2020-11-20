package com.hwhhhh.taskmanager.util;

import android.os.Message;

import com.hwhhhh.taskmanager.dto.TaskDto;

public class MessageEvent {
    public static final int CREATE = 1;
    public static final int EDIT = 2;
    public static final int DELETE = 3;
    public static final int ADD = 4;
    public static final int INIT_BUTTON = 5;
    public static final int INIT_DELETE = 6;

    private int type;
    private TaskDto taskDto;

    public MessageEvent(int type) {
        this.type = type;
    }

    public MessageEvent(int type, TaskDto taskDto) {
        this.type = type;
        this.taskDto = taskDto;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public TaskDto getTaskDto() {
        return taskDto;
    }

    public void setTaskDto(TaskDto taskDto) {
        this.taskDto = taskDto;
    }
}
