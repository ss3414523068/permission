package com.mapper;

import com.model.Permission;
import com.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionMapper {
    
    List<Permission> selectPermissionList(User user);
    
}