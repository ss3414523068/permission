package com.module.demo.controller.back;

import com.module.demo.mapper.PermissionMapper;
import com.module.demo.mapper.RoleMapper;
import com.module.demo.model.Permission;
import com.module.demo.model.Role;
import com.module.demo.model.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javautil.security.JWT;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Api(description = "后台")
@RestController
@RequestMapping("/back")
public class BackController {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @ApiOperation("后台首页")
    @GetMapping("/home")
    public Map home() {
        /* 根据用户拥有的权限决定其内容 */
        Subject subject = SecurityUtils.getSubject();
        String name = JWT.getName((String) subject.getPrincipal());
        User select = new User();
        select.setUserName(name);
        List<Role> roleList = roleMapper.selectRoleList(select);
        List<Permission> permissionList = permissionMapper.selectPermissionList(select);
        Map result = new LinkedHashMap();
        result.put("msg", "后台首页");
        Map data = new LinkedHashMap();
        data.put("roleList", roleList);
        data.put("permissionList", permissionList);
        result.put("data", data);
        return result;
    }

}
