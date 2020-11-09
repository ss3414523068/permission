package com.module.demo.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/list")
    @RequiresPermissions("user:list")
    public Map list() {
        Map result = new HashMap();
        result.put("msg", "user:list");
        return result;
    }

    @GetMapping("/create")
    @RequiresPermissions("user:create")
    public Map create() {
        Map result = new HashMap();
        result.put("msg", "user:create");
        return result;
    }

    @GetMapping("/get")
    @RequiresPermissions("user:get")
    public Map get() {
        Map result = new HashMap();
        result.put("msg", "user:get");
        return result;
    }

    @GetMapping("/update")
    @RequiresPermissions("user:update")
    public Map update() {
        Map result = new HashMap();
        result.put("msg", "user:update");
        return result;
    }

    @GetMapping("/delete")
    @RequiresPermissions("user:delete")
    public Map delete() {
        Map result = new HashMap();
        result.put("msg", "user:delete");
        return result;
    }

}
