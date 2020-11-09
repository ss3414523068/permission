package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("")
public class IndexController {

    /* URL回写禁用jsp:forward指令 */
    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("redirect:/home");
    }

    @RequestMapping("/home")
    public ModelAndView home(HttpServletResponse response) {
        return new ModelAndView("/home");
    }

    @ResponseBody
    @RequestMapping("/cookie")
    public Map cookie(HttpServletRequest request) {
        /*
         * ①第一次访问，没有Session自动创建
         * ②重复访问，根据Cookie中的SessionId获取Session（浏览器允许Cookie的情况下）
         * ③Session无状态，有超时时间，由系统管理
         * */
        HttpSession session = request.getSession();
        Map result = new HashMap();
        result.put("sessionId", session.getId() != null ? session.getId() : "sessionId");
        return result;
    }

}
