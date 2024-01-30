package edu.vt.ranhuo.codewavecommon.model.dto;

import lombok.Data;

@Data
public class TaskUpdate {
    private Integer id;
    private String task_result;
    private String status;
    private String job_id;
}
