package edu.vt.ranhuo.codewaveserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.vt.ranhuo.codewavecommon.model.dto.JobRequest;
import edu.vt.ranhuo.codewavecommon.model.entity.Job;
import edu.vt.ranhuo.codewavecommon.model.entity.Task;
import edu.vt.ranhuo.codewaveserver.mapper.TaskMapper;
import edu.vt.ranhuo.codewaveserver.service.JobService;
import edu.vt.ranhuo.codewaveserver.mapper.JobMapper;
import edu.vt.ranhuo.codewavecommon.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
* @author yukun
* @description 针对表【job】的数据库操作Service实现
* @createDate 2024-01-16 13:43:52
*/
@Service
public class JobServiceImpl extends ServiceImpl<JobMapper, Job>
    implements JobService{
    @Autowired
    private TaskMapper taskMapper;
    @Transactional
    @Override
    public void addNewJob(JobRequest jobRequest) {
        Job job= BeanCopyUtils.copyBean(jobRequest,Job.class);
        save(job);
        for(String s: splitStringIntoSegments(job.getJob_input(),10)){
            Task task = new Task();
            task.setTask_input(s);
            task.setStatus("ready");
            taskMapper.insert(task);
            System.out.println(s);
        }
    }

    public static List<String> splitStringIntoSegments(String s, int len) {
        List<String> segments = new ArrayList<>();
        int start = 0;
        while (start < s.length()) {
            int end = Math.min(start + len, s.length());
            segments.add(s.substring(start, end));
            start = end;
        }
        return segments;
    }
}




