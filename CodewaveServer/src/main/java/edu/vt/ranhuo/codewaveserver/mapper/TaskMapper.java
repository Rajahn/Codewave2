package edu.vt.ranhuo.codewaveserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.vt.ranhuo.codewavecommon.model.entity.Task;
import org.apache.ibatis.annotations.Mapper;

/**
* @author yukun
* @description 针对表【task】的数据库操作Mapper
* @createDate 2024-01-16 13:43:00
* @Entity edu.vt.ranhuo.codewaveserver.model.entity.Task
*/
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    Task selectByTaskId(Integer id);
}




