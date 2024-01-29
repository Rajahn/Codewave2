package edu.vt.ranhuo.codewaveserver.controller;

import edu.vt.ranhuo.codewavecommon.model.dto.JobRequest;
import edu.vt.ranhuo.codewavecommon.common.BaseResponse;
import edu.vt.ranhuo.codewavecommon.common.ResultUtils;
import edu.vt.ranhuo.codewaveserver.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @PostMapping("/create_job")
    public BaseResponse createJob(@RequestBody JobRequest jobRequest){
        jobService.addNewJob(jobRequest);
        return ResultUtils.success("ok");
    }
}
