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
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 
     */
    @TableId
    private Integer id;

    /**
     * email account
     */
    private String user_account;

    /**
     * 
     */
    private String user_password;

    /**
     * 
     */
    private String user_name;

    /**
     * -1 0 1 2 3 封禁，普通用户，会员，超级会员，管理员
     */
    private Integer user_role;

    /**
     * 
     */
    private Date create_time;

    /**
     * 
     */
    private Date update_time;

    /**
     * 随userRole更新，代表优先多少秒执行
     */
    private Integer priority;

    /**
     * 每天剩余可以执行task的数量
     */
    private Integer remain_token_nums;

    /**
     * 
     */
    private Integer used_token_nums;

    /**
     * logic delete
     */
    private Integer is_delete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}