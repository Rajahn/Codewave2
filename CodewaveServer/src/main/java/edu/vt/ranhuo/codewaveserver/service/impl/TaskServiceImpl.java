package edu.vt.ranhuo.codewaveserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import edu.vt.ranhuo.asynccore.enums.QueueType;
import edu.vt.ranhuo.asyncmaster.context.IMaster;
import edu.vt.ranhuo.codewavecommon.common.ErrorCode;
import edu.vt.ranhuo.codewavecommon.model.dto.TaskUpdate;
import edu.vt.ranhuo.codewavecommon.model.entity.Task;
import edu.vt.ranhuo.codewavecommon.common.BaseResponse;
import edu.vt.ranhuo.codewavecommon.common.ResultUtils;
import edu.vt.ranhuo.codewavecommon.utils.S3Utils;
import edu.vt.ranhuo.codewaveserver.service.TaskService;
import edu.vt.ranhuo.codewaveserver.mapper.TaskMapper;
import edu.vt.ranhuo.codewavecommon.utils.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.redisson.api.RedissonClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author yukun
 * @description 针对表【task】的数据库操作Service实现
 * @createDate 2024-01-16 13:43:00
 */
@Service
@Slf4j
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
        implements TaskService {

    private Gson gson = new Gson();

    @Autowired
    private IMaster<String> master;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Scheduled(fixedRate = 5000) //
    public void sendTaskToRedis() {
        int curRedayTask = master.getQueueSize(QueueType.HIGN);
        int slaveNodeNum = master.getActiveNodeInfo().size() - 1;

        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "ready");
        List<Task> taskList = taskMapper.selectList(queryWrapper);
        log.info("curReady num is :{}, slaveNode num is :{}  taskList: {}", curRedayTask, slaveNodeNum, taskList);
        for (int i = 0; i < slaveNodeNum*2 - curRedayTask && i < taskList.size(); i++) {

            Task task = taskList.get(i);
            task.setOrder_time(System.currentTimeMillis());
            task.setStatus("running");
            taskMapper.updateById(task);
            String taskstr = gson.toJson(task);
            master.send(QueueType.HIGN, task.getOrder_time(), taskstr);
            log.info("send task to redis: {}", taskstr);
        }
    }

    @Scheduled(fixedRate = 5000)
    public void commitTask(){
        Optional<TaskUpdate> result = master.consume().map(value -> {
            String content = value;
            //log.warn("poll content: {}", content);
            try {
                return gson.fromJson(content, TaskUpdate.class);
            } catch (JsonSyntaxException e) {
                log.error("JSON parsing error", e);
                return null;
            }
        });

        result.ifPresent(taskUpdate -> {
            log.info("taskUpdate is {}", taskUpdate);
            Task task = BeanCopyUtils.copyBean(taskUpdate, Task.class);
            task.setStatus("finished");
            taskMapper.updateById(task);
            master.commit(gson.toJson(taskUpdate));
            markTaskAsFinished(task.getJob_id());
        });
    }

    public void markTaskAsFinished(String jobId) {
        RAtomicLong tasksCount = redissonClient.getAtomicLong("job:" + jobId + ":tasksCount");
        log.warn("job {} finished, tasksCount: {}", jobId, tasksCount);
        if (tasksCount.decrementAndGet() == 0) {
            // 所有任务都完成了
            handleAllTasksFinished(jobId);
            tasksCount.delete();
        }
    }

    private void handleAllTasksFinished(String jobId) {
        // 执行 job 完成后的逻辑
        log.warn("all job {} finished,combain result and update job table", jobId);
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("job_id", jobId);
        queryWrapper.orderByAsc("id");
        List<Task> taskList = taskMapper.selectList(queryWrapper);

        if (taskList.isEmpty()) {
            System.out.println("No tasks found for job_id");
            return;
        }

        String directoryPath = System.getProperty("java.io.tmpdir"); // 使用系统临时目录
        String fileName = jobId + ".mp3";
        Path filePath = Paths.get(directoryPath, fileName);

        try {
            Files.createDirectories(filePath.getParent()); // 确保目录存在

            String result = combineTaskResults(taskList); // 假设这个方法组合所有任务的结果
            byte[] audioByte = Base64.getDecoder().decode(result);

            try (FileOutputStream outputStream = new FileOutputStream(filePath.toFile())) {
                outputStream.write(audioByte);
                System.out.println("Audio file written to: " + filePath);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Base64 encoding: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error writing audio file: " + e.getMessage());
        }

        S3Utils s3Utils = new S3Utils();
        String presignedUrl = s3Utils.uploadFile(String.valueOf(filePath), fileName);
        log.warn("Presigned URL: " + presignedUrl);

        boolean isDeleted = deleteTempFile(String.valueOf(filePath));
        if (isDeleted) {
            System.out.println("Temp file deleted successfully.");
        } else {
            System.out.println("Failed to delete temp file.");
        }
    }

    public boolean deleteTempFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete(); // 尝试删除文件，成功时返回 true
        } else {
            System.out.println("File not found: " + filePath);
            return false;
        }
    }


    private String combineTaskResults(List<Task> taskList) {
        String result = "";
        for (Task task : taskList) {
            // 这里假设 task.getTask_result() 返回有效的 Base64 编码字符串
            result += task.getTask_result();
        }
        return result;
    }

    @Override
    public BaseResponse assignTask() {
        RLock lock = redissonClient.getLock("taskLock");
        Task task = null;
        try {
            // 尝试获取锁，最多等待100秒，锁定后10秒自动释放
            boolean isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!isLocked) {
                log.info("获取锁失败");
                return ResultUtils.success(task);
            }

            // 执行任务分配逻辑
            QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", "ready") // 等于ready状态
                    .orderByAsc("order_time") // 按order_time升序排序
                    .last("LIMIT 1"); // 限制结果只有一条记录
            task = taskMapper.selectOne(queryWrapper);

            if (task != null) {
                // 在Redis中记录任务的过期时间
                String taskKey = "task:" + task.getId();
                long expirationTime = 10; // 假设任务过期时间为60秒
                RBucket<String> bucket = redissonClient.getBucket(taskKey);
                bucket.set(String.valueOf(task.getId()), expirationTime, TimeUnit.SECONDS);
            }

            return ResultUtils.success(task);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResultUtils.error(ErrorCode.OPERATION_ERROR);
        } finally {
            // 释放锁
            lock.unlock();
        }
    }

    @Override
    public BaseResponse updateTask(TaskUpdate taskUpdate) {
        Task task = BeanCopyUtils.copyBean(taskUpdate, Task.class);
        int id = taskMapper.updateById(task);
        return ResultUtils.success(id);
    }
}




