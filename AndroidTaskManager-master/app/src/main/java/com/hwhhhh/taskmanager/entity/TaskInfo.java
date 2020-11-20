package com.hwhhhh.taskmanager.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class TaskInfo implements Serializable {
    private int id;     //主键
    private String taskTitle;       //标题
    private String startTime;         //开始时间
    private String endTime;           //结束时间
    private String taskLevel;          //任务等级
    private String completeStatus;          //完成情况

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTaskLevel() {
        return taskLevel;
    }

    public void setTaskLevel(String taskLevel) {
        this.taskLevel = taskLevel;
    }

    public String getCompleteStatus() {
        return completeStatus;
    }

    public void setCompleteStatus(String completeStatus) {
        this.completeStatus = completeStatus;
    }
}
