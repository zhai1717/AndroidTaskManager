package com.hwhhhh.taskmanager.dto;

import com.hwhhhh.taskmanager.entity.TaskContent;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TaskDto implements Serializable {
    //这些字段为TaskInfo里的字段，不可用TaskInfo类直接替代，否则无法将json转化为实体类
    private int id;     //主键
    private String taskTitle;       //标题
    private String startTime;         //开始时间
    private String endTime;           //结束时间
    private String taskLevel;          //任务等级
    private String completeStatus;          //完成情况
    //此为TaskContent，一个任务可对应多个TaskContent
    private List<TaskContent> taskContentBeanList;

    public TaskDto() {
        this.taskTitle = "";
        this.startTime = "";
        this.endTime = "";
        this.taskTitle = "";
        this.taskContentBeanList = new ArrayList<>();
        taskContentBeanList.add(new TaskContent());
    }

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

    public List<TaskContent> getTaskContentBeanList() {
        return taskContentBeanList;
    }

    public void setTaskContentBeanList(List<TaskContent> taskContentBeanList) {
        this.taskContentBeanList = taskContentBeanList;
    }

    public String toString() {
        return this.taskContentBeanList.toString();
    }

    public void synchronizationContent(List<String> list) {
        int extraTasks = list.size();
        if (extraTasks >= this.taskContentBeanList.size()) {    //当新的任务内容大于等于原本的
            while (extraTasks > this.taskContentBeanList.size()) {  //则添加
                this.taskContentBeanList.add(new TaskContent());
            }
            for (int i = 0; i < this.taskContentBeanList.size(); i++) {     //同步信息
                this.taskContentBeanList.get(i).setTaskContent(list.get(i));
            }
        } else {
            while (extraTasks < this.taskContentBeanList.size()) {      //新的任务内容小于
                this.taskContentBeanList.remove(this.taskContentBeanList.size() - 1);
            }
            for (int i = 0; i < this.taskContentBeanList.size(); i ++ ){        //同步
                this.taskContentBeanList.get(i).setTaskContent(list.get(i));
            }
        }
    }
}
