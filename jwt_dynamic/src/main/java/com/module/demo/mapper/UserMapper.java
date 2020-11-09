package com.module.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.module.demo.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper extends BaseMapper<User> {

}
