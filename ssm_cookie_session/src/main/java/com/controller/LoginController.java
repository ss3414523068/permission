package com.controller;

import com.mapper.UserMapper;
import com.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    /*
     * 登录页面
     * ①已登录状态下重复访问直接跳转后台
     * ②可以多人同时登录（根据JSessionId区分）
     * */
    @RequestMapping("/login")
    public ModelAndView login(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            Cookie[] cookies = request.getCookies();
            for (int i = 0; i < cookies.length; i++) {
                if ("userId".equals(cookies[i].getName()) && user.getId().toString().equals(cookies[i].getValue())) {
                    return new ModelAndView("redirect:/back/home");
                } else {
                    return new ModelAndView("/login/login");
                }
            }
        }
        return new ModelAndView("/login/login");
    }

    /* 登录失败页面 */
    @RequestMapping("/loginFailure")
    public ModelAndView loginFailure() {
        return new ModelAndView("/login/loginFailure");
    }

    /* 未登录/没有权限页面 */
    @RequestMapping("/noPermission")
    public ModelAndView noPermission() {
        return new ModelAndView("/login/noPermission");
    }

    /*
     * 登录行为
     * ①登录成功跳转后台，登录失败跳转失败
     * */
    @RequestMapping("/doLogin")
    public ModelAndView doLogin(HttpServletRequest request, HttpServletResponse response, User select) {
        User user = userMapper.selectUser(select);

        ModelAndView view = new ModelAndView();
        if (user != null) {
            /*
             * 登录成功
             * ①将User写入Session，将userId写入Cookie
             * ②Session在用户第一次访问时就自动创建了（根据JSESSIONID区分不同用户）
             * ③跳转后台
             * */
            request.getSession().setAttribute("user", user);
            Cookie cookie = new Cookie("userId", user.getId().toString());
            response.addCookie(cookie);
            view.setViewName("redirect:/back/home");
        } else {
            view.setViewName("redirect:/login/loginFailure"); /* 登录失败页面 */
        }
        return view;
    }

    /*
     * 注销
     * ①在已登录状态下才能访问
     * */
    @RequestMapping("/logout")
    public ModelAndView logout(HttpServletRequest request) {
        request.getSession().invalidate(); /* 删除整个Session */
        return new ModelAndView("/login/login");
    }

}
