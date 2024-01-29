package edu.vt.ranhuo.codewaveworker.service;

import com.google.gson.Gson;
import edu.vt.ranhuo.codewavecommon.common.BaseResponse;
import edu.vt.ranhuo.codewavecommon.model.dto.TaskUpdate;
import edu.vt.ranhuo.codewavecommon.model.entity.Task;
import edu.vt.ranhuo.codewavecommon.utils.BeanCopyUtils;
import edu.vt.ranhuo.codewaveworker.statemachine.TaskCallback;
import okhttp3.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TranslateTaskService implements TaskCallback {
    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();


    @Override
    public Task onPollTask() {
        Task task = null;
        Request request = new Request.Builder()
                .url("http://localhost:8081/task/requireTask")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                BaseResponse baseResponse = gson.fromJson(responseBody, BaseResponse.class);
                if (baseResponse != null && baseResponse.getData() != null) {
                    task = gson.fromJson(gson.toJson(baseResponse.getData()), Task.class);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return task;
    }

    @Override
    public TaskUpdate onProcessingTask(Task task) {
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String res = simpleDateFormat.format(new Date(timestamp));

        TaskUpdate tu = BeanCopyUtils.copyBean(task,TaskUpdate.class);
        tu.setTask_result("res");
        tu.setId(task.getId());
        tu.setStatus("finished");
        return tu;
    }


    @Override
    public void onPostTaskUpdate(Task task,TaskUpdate taskUpdate) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = gson.toJson(taskUpdate);
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url("http://localhost:8081/task/updateTask")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            // 处理响应
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onPostError(Exception e) {

    }
}
