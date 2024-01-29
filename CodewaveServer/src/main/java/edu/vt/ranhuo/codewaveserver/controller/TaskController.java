package edu.vt.ranhuo.codewaveserver.controller;

import edu.vt.ranhuo.codewavecommon.model.dto.TaskUpdate;
import edu.vt.ranhuo.codewavecommon.common.BaseResponse;
import edu.vt.ranhuo.codewaveserver.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/requireTask")
    public BaseResponse requireTask(){
        return taskService.assignTask();
    }

    @PostMapping("/updateTask")
    public BaseResponse updateTask(@RequestBody TaskUpdate taskUpdate){

        return taskService.updateTask(taskUpdate);
    }
}
