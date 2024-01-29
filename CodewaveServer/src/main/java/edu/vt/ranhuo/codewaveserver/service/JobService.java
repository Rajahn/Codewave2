package edu.vt.ranhuo.codewaveserver.service;

import edu.vt.ranhuo.codewavecommon.model.dto.JobRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import edu.vt.ranhuo.codewavecommon.model.entity.Job;

/**
* @author yukun
* @description 针对表【job】的数据库操作Service
* @createDate 2024-01-16 13:43:52
*/
public interface JobService extends IService<Job> {

    void addNewJob(JobRequest jobRequest);
}
