package edu.vt.ranhuo.codewaveserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.vt.ranhuo.codewavecommon.common.ErrorCode;
import edu.vt.ranhuo.codewavecommon.model.dto.TaskUpdate;
import edu.vt.ranhuo.codewavecommon.model.entity.Task;
import edu.vt.ranhuo.codewavecommon.common.BaseResponse;
import edu.vt.ranhuo.codewavecommon.common.ResultUtils;
import edu.vt.ranhuo.codewaveserver.service.TaskService;
import edu.vt.ranhuo.codewaveserver.mapper.TaskMapper;
import edu.vt.ranhuo.codewavecommon.utils.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
* @author yukun
* @description 针对表【task】的数据库操作Service实现
* @createDate 2024-01-16 13:43:00
*/
@Service
@Slf4j
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task>
    implements TaskService{
    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private RedissonClient redissonClient;
    @Override
    public BaseResponse assignTask() {
        RLock lock = redissonClient.getLock("taskLock");
        Task task = null;
        try {
            // 尝试获取锁，最多等待100秒，锁定后10秒自动释放
            boolean isLocked = lock.tryLock(5, 3, TimeUnit.SECONDS);
            if (!isLocked) {
                log.info("获取锁失败");
                return  ResultUtils.success(task);
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
        Task task = BeanCopyUtils.copyBean(taskUpdate,Task.class);
        int id = taskMapper.updateById(task);
        return ResultUtils.success(id);
    }
}




