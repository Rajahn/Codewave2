package edu.vt.ranhuo.codewaveworker.service;

import com.google.gson.Gson;
import edu.vt.ranhuo.codewavecommon.common.BaseResponse;
import edu.vt.ranhuo.codewavecommon.model.dto.TaskUpdate;
import edu.vt.ranhuo.codewavecommon.model.entity.Task;


import edu.vt.ranhuo.codewaveworker.statemachine.states.PendingState;
import edu.vt.ranhuo.codewaveworker.statemachine.TaskContext;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TaskPollingService {
    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();

    private TaskContext context;

    public TaskPollingService() {
        this.context = new TaskContext(this);
        context.setState(new PendingState());
    }

    public void pollForTask()  {
        Request request = new Request.Builder()
                .url("http://localhost:8081/task/requireTask")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                BaseResponse baseResponse = gson.fromJson(responseBody, BaseResponse.class);
                if (baseResponse != null && baseResponse.getData() != null) {
                    Task task = gson.fromJson(gson.toJson(baseResponse.getData()), Task.class);
                    TaskUpdate taskUpdate = process(task);
                    postTaskUpdate(taskUpdate);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void postTaskUpdate(TaskUpdate taskUpdate) throws IOException {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = gson.toJson(taskUpdate);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url("http://localhost:8081/task/updateTask")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // 处理响应
        }
    }

    private TaskUpdate process(Task task) {
        // 处理数据并返回TaskUpdate
        //System.out.println(data);

        task.setTask_result("hahaha");
        TaskUpdate tu = new TaskUpdate();
        tu.setTask_result("hahaha");
        tu.setId(task.getId());
        tu.setStatus("finished");
        return tu;
    }
}




