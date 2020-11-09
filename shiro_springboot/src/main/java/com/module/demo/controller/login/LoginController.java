package com.module.demo.controller.login;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    /* 登录页面 */
    @GetMapping("/login")
    public ModelAndView login() {
        ModelAndView view = new ModelAndView();
        Subject subject = SecurityUtils.getSubject();
        if (subject != null && subject.isAuthenticated()) {
            view.setViewName("redirect:/back/home"); /* 已登录状态下重复访问直接跳转后台 */
        } else {
            view.setViewName("/login/login");
        }
        return view;
    }

    /* 登录行为 */
    @PostMapping("/doLogin")
    public ModelAndView doLogin(String name, String password) {
        UsernamePasswordToken token = new UsernamePasswordToken(name, password);
        Subject subject = SecurityUtils.getSubject();

        ModelAndView view = new ModelAndView();
        try {
            subject.login(token); /* 调用RBACRealm.doGetAuthenticationInfo() */
            view.setViewName("redirect:/back/home");
        } catch (Exception e) {
            view.setViewName("redirect:/login/loginFailure");
        }
        return view;
    }

    /* 登录失败 */
    @GetMapping("/loginFailure")
    public Map loginFailure() {
        Map result = new HashMap();
        result.put("msg", "用户不存在/密码错误");
        return result;
    }

    /* 没有权限 */
    @GetMapping("/noPermission")
    public Map noPermission() {
        Map result = new HashMap();
        result.put("msg", "没有权限");
        return result;
    }

    /* 直接登录 */
    @GetMapping("/logged")
    public ModelAndView logged(String para1, String para2) {
        UsernamePasswordToken token = new UsernamePasswordToken("logged", "123456");
        Subject subject = SecurityUtils.getSubject();

        ModelAndView view = new ModelAndView();
        try {
            subject.login(token);
            view.setViewName("redirect:/back/home");
        } catch (Exception e) {
            view.setViewName("redirect:/login/loginFailure");
        }
        return view;
    }

}
