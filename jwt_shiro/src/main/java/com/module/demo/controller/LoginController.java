package com.module.demo.controller;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.common.util.JWTUtil;
import com.module.demo.mapper.UserMapper;
import com.module.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    /* 登录行为 */
    @PostMapping("/doLogin")
    public Map doLogin(String name, String password) {
        User user = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getUserName, name));

        Map result = new HashMap();
        if (user == null) {
            result.put("msg", "用户不存在");
        } else if (!user.getUserPassword().equals(SecureUtil.md5(password))) {
            result.put("msg", "密码错误");
        } else {
            result.put("msg", "登录成功");
            result.put("token", JWTUtil.sign(name, SecureUtil.md5(password)));
        }
        return result;
    }

    @GetMapping("/exist")
    public Map exist() {
        Map result = new HashMap();
        result.put("msg", "用户不存在");
        return result;
    }

    @GetMapping("/error")
    public Map error() {
        Map result = new HashMap();
        result.put("msg", "密码错误");
        return result;
    }

    @GetMapping("/invalid")
    public Map invalid() {
        Map result = new HashMap();
        result.put("msg", "token无效");
        return result;
    }

}
