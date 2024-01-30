package edu.vt.ranhuo.codewaveworker.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import edu.vt.ranhuo.asyncslave.context.ISlave;
import edu.vt.ranhuo.asyncslave.context.Slave;
import edu.vt.ranhuo.codewavecommon.common.BaseResponse;
import edu.vt.ranhuo.codewavecommon.common.ErrorCode;
import edu.vt.ranhuo.codewavecommon.model.dto.TaskUpdate;
import edu.vt.ranhuo.codewavecommon.model.entity.Task;


import edu.vt.ranhuo.codewavecommon.utils.BeanCopyUtils;
import edu.vt.ranhuo.codewaveworker.statemachine.TaskCallback;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class TextToSoundTaskService implements TaskCallback {

    private OkHttpClient client = new OkHttpClient();
    private Gson gson = new Gson();


//    @Override
//    public Task onPollTask() {
//        Task task = null;
//        Request request = new Request.Builder()
//                .url("http://localhost:8081/task/requireTask")
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful() && response.body() != null) {
//                String responseBody = response.body().string();
//                BaseResponse baseResponse = gson.fromJson(responseBody, BaseResponse.class);
//                if (baseResponse != null && baseResponse.getData() != null) {
//                    task = gson.fromJson(gson.toJson(baseResponse.getData()), Task.class);
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return task;
//    }

    @Override
    public Task onPollTask() {

        Optional<Task> result = slave.consume().map(value -> {
            String content = (String) value;
            log.warn("poll content: {}", content);
            try {
                return gson.fromJson(content, Task.class);
            } catch (JsonSyntaxException e) {
                log.error("JSON parsing error", e);
                return null;
            }
        });

        return result.orElse(null);
    }

    @Override
    public TaskUpdate onProcessingTask(Task task) {
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String res = simpleDateFormat.format(new Date(timestamp));
        res = "haha "+res;
        TaskUpdate tu = BeanCopyUtils.copyBean(task, TaskUpdate.class);
        tu.setTask_result(res);
        tu.setId(task.getId());
        tu.setStatus("finished");
        log.info("task update is {}", tu);
        return tu;
    }


    @Override
    public void onPostTaskUpdate(Task task, TaskUpdate taskUpdate) {
          String originStr = gson.toJson(task);
          String updateStr = gson.toJson(taskUpdate);
          slave.commit(updateStr,originStr);
          log.info("commit task update is {}", taskUpdate);
    }

    @Override
    public void onPostError(Exception e) {

    }
}




