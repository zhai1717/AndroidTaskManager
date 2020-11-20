package com.hwhhhh.taskmanager.service.ServiceImpl;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hwhhhh.taskmanager.util.HttpCallBack;
import com.hwhhhh.taskmanager.util.HttpUtil;
import com.hwhhhh.taskmanager.util.JSONResult;
import com.hwhhhh.taskmanager.dto.TaskDto;
import com.hwhhhh.taskmanager.service.TaskService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class TaskServiceImpl implements TaskService {
    private static final String TAG = "TaskServiceImpl";
    private List<TaskDto> taskDtoList;
    private JSONResult<String> result;
    @Override
    public List<TaskDto> getAllTask(HttpCallBack httpCallBack) {
        httpCallBack.load();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUtil httpUtil = new HttpUtil();
                    String response = httpUtil.doGet(GET_ALLTASK);
                    JSONResult<List<TaskDto>> jsonResult = jsonToEntity(response);
                    callback(jsonResult.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();       //需要并入主线程，否则返回为空
            httpCallBack.success();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return taskDtoList;
    }

    @Override
    public void createTask(final TaskDto taskDto) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUtil httpUtil = new HttpUtil();
                    String response = httpUtil.doPostTask(POST_TASK, entityToJson(taskDto));
                    Gson gson = new Gson();
                    Type type = new TypeToken<JSONResult<String>>(){}.getType();
                    JSONResult<String> json = gson.fromJson(response, type);
                    callback(json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 回调函数，将解析json得到的JSONResult返回给本类并返回。
     * @param data    即json对应的JSONResult
     */
    private void callback(List<TaskDto> data) {
        this.taskDtoList = data;
    }

    /**
     * 尝试万能的回调
     * @param result 返回的结果
     */
    private void callback(JSONResult<String> result) {
        this.result = result;
    }
    private JSONResult<List<TaskDto>> jsonToEntity(String response) {
        Gson gson = new Gson();
        Type type = new TypeToken<JSONResult<List<TaskDto>>>(){}.getType();
        return gson.fromJson(response, type);
    }

    private String entityToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public JSONResult<String> getResult() {
        return result;
    }

    @Override
    public void deleteTask(final int id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUtil httpUtil = new HttpUtil();
                    httpUtil.doDeleteTask(DELETE_TASK + id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void modifyTask(final TaskDto taskDto) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String json = entityToJson(taskDto);
                    HttpUtil httpUtil = new HttpUtil();
                    httpUtil.doPutTask(POST_TASK, json);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
