package edu.vt.ranhuo.codewaveserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.vt.ranhuo.codewavecommon.model.entity.Job;
import org.apache.ibatis.annotations.Mapper;

/**
* @author yukun
* @description 针对表【job】的数据库操作Mapper
* @createDate 2024-01-16 13:43:52
* @Entity edu.vt.ranhuo.codewaveserver.model.entity.Job
*/
@Mapper
public interface JobMapper extends BaseMapper<Job> {

}




