package edu.vt.ranhuo.codewaveserver.mapper;

import edu.vt.ranhuo.codewaveserver.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author yukun
* @description 针对表【user】的数据库操作Mapper
* @createDate 2024-01-16 13:40:40
* @Entity edu.vt.ranhuo.codewaveserver.model.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




