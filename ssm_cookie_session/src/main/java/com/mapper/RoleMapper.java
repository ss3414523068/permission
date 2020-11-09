package com.mapper;

import com.model.Role;
import com.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMapper {

    List<Role> selectRoleList(User user);

}
