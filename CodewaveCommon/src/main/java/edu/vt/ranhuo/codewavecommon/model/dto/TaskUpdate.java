package edu.vt.ranhuo.codewaveserver.model.dto;

import lombok.Data;

@Data
public class TaskUpdate {
    private Integer id;
    private String task_result;
    private String status;
}
