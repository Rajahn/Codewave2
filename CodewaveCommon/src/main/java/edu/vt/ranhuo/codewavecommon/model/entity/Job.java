package edu.vt.ranhuo.codewaveserver.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName job
 */
@TableName(value ="job")
@Data
public class Job implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer user_id;

    /**
     * 
     */
    private String job_type;

    /**
     * 
     */
    private String job_input;

    /**
     * result url
     */
    private String job_result;

    /**
     * 
     */
    private String job_metainfo;

    /**
     * 
     */
    private Integer sub_task_num;

    /**
     * 
     */
    private Integer sub_task_finished_num;

    /**
     * 
     */
    private Integer priority;

    /**
     * running, succeed, failed, retry
     */
    private String status;

    /**
     * 0 1 2 3 表示一共有几个阶段，应当从config根据type查出
     */
    private Integer stages_num;

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