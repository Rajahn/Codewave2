package edu.vt.ranhuo.codewaveserver.service;

import edu.vt.ranhuo.codewavecommon.model.dto.TaskUpdate;
import edu.vt.ranhuo.codewavecommon.model.entity.Task;
import edu.vt.ranhuo.codewavecommon.common.BaseResponse;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author yukun
* @description 针对表【task】的数据库操作Service
* @createDate 2024-01-16 13:43:00
*/
public interface TaskService extends IService<Task> {

    BaseResponse assignTask();

    BaseResponse updateTask(TaskUpdate taskUpdate);
}
