package com.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @ResponseBody
    @RequestMapping("/list")
    @RequiresPermissions("user:list")
    public Map list() {
        Map result = new HashMap();
        result.put("msg", "/user/list");
        return result;
    }

    @ResponseBody
    @RequestMapping("/create")
    @RequiresPermissions("user:create")
    public Map create() {
        Map result = new HashMap();
        result.put("msg", "/user/create");
        return result;
    }

    @ResponseBody
    @RequestMapping("/get")
    @RequiresPermissions("user:get")
    public Map get() {
        Map result = new HashMap();
        result.put("msg", "/user/get");
        return result;
    }

    @ResponseBody
    @RequestMapping("/update")
    @RequiresPermissions("user:update")
    public Map update() {
        Map result = new HashMap();
        result.put("msg", "/user/update");
        return result;
    }

    @ResponseBody
    @RequestMapping("/delete")
    @RequiresPermissions("user:delete")
    public Map delete() {
        Map result = new HashMap();
        result.put("msg", "/user/delete");
        return result;
    }

}
