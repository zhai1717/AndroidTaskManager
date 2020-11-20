package com.hwhhhh.taskmanager.util;

import com.hwhhhh.taskmanager.dto.TaskDto;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    private static final String BASE_URL = "http://www.hwhhhh.com:8088/apis/";
    private OkHttpClient client = new OkHttpClient();

    public String doGet(String url) throws IOException{
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            return null;
        }
    }

    public String doPostTask(String url, String json) throws IOException{
        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            return null;
        }
    }

    public String doDeleteTask(String url) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .delete()
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            return null;
        }
    }

    public String doPutTask(String url, String json) throws IOException {
        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .put(body)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            return null;
        }
    }
}
