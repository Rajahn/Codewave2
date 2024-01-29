package edu.vt.ranhuo.codewaveserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.vt.ranhuo.codewaveserver.model.entity.User;
import edu.vt.ranhuo.codewaveserver.service.UserService;
import edu.vt.ranhuo.codewaveserver.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author yukun
* @description 针对表【user】的数据库操作Service实现
* @createDate 2024-01-16 13:40:40
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




