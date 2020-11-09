package com.controller;

import com.mapper.PermissionMapper;
import com.mapper.RoleMapper;
import com.model.Permission;
import com.model.Role;
import com.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/back")
public class BackController {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @RequestMapping("/home")
    public ModelAndView home(HttpServletRequest request) {
        ModelAndView view = new ModelAndView();
        /* 根据用户拥有的权限决定其内容 */
        User select = (User) request.getSession().getAttribute("user");

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request2 = attributes.getRequest();

        List<Role> roleList = roleMapper.selectRoleList(select);
        List<Permission> permissionList = permissionMapper.selectPermissionList(select);
        view.addObject("roleList", roleList);
        view.addObject("permissionList", permissionList);
        view.setViewName("/back/home");
        return view;
    }

}
