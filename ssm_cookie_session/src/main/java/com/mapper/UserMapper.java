package com.mapper;

import com.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {

    /* 查询是否存在此用户 */
    User selectUser(User user);

}
