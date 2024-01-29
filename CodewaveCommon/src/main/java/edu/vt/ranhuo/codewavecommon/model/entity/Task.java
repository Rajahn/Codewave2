package edu.vt.ranhuo.codewavecommon.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName task
 */
@TableName(value ="task")
@Data
public class Task implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer job_id;

    /**
     * 
     */
    private String status;

    /**
     * 
     */
    private String task_input;

    /**
     * 
     */
    private String task_result;

    /**
     * 
     */
    private Date create_time;

    /**
     * 
     */
    private Date update_time;

    /**
     * 
     */
    private Long order_time;

    /**
     * 
     */
    private Integer retry_num;

    /**
     * 
     */
    private Integer max_retry_num;

    /**
     * 
     */
    private Integer max_retry_interval;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}