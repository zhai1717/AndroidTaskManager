package com.hwhhhh.taskmanager.entity;

import java.io.Serializable;

public class TaskContent implements Serializable {
    private int contentId;
    private int taskId;
    private String taskContent;
    private int isFinished;

    public TaskContent() {
        this.taskContent = "";
    }

    public TaskContent(String content) {
        this.taskContent = content;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }

    public int getIsFinished() {
        return isFinished;
    }

    public void setIsFinished(int isFinished) {
        this.isFinished = isFinished;
    }

    public String toString() {
        return this.taskContent;
    }
}
