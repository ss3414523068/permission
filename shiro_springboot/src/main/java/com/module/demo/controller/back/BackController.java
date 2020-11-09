package com.module.demo.controller.back;

import com.module.demo.mapper.PermissionMapper;
import com.module.demo.mapper.RoleMapper;
import com.module.demo.model.Permission;
import com.module.demo.model.Role;
import com.module.demo.model.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/back")
public class BackController {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    /* 后台首页（登录后就允许访问） */
    @GetMapping("/home")
    public ModelAndView home() {
        /* 根据用户拥有的权限决定其内容 */
        Subject subject = SecurityUtils.getSubject();
        User select = (User) subject.getPrincipal();
        List<Role> roleList = roleMapper.selectRoleList(select);
        List<Permission> permissionList = permissionMapper.selectPermissionList(select);
        ModelAndView view = new ModelAndView();
        view.addObject("roleList", roleList);
        view.addObject("permissionList", permissionList);
        view.setViewName("/back/home");
        return view;
    }

}
