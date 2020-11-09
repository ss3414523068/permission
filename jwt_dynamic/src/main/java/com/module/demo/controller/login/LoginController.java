package com.module.demo.controller.login;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.common.util.RedisUtil;
import com.module.demo.mapper.UserMapper;
import com.module.demo.model.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import javautil.security.JWT;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Api(description = "登录")
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    @Value("${redis.open}")
    private Boolean redisOpen;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CacheManager cacheManager;

    @ApiOperation("登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户名", required = true),
            @ApiImplicitParam(name = "password", value = "用户密码", required = true),
    })
    @PostMapping("/doLogin")
    public Map doLogin(String name, String password) {
        User user = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getUserName, name));

        Map result = new LinkedHashMap();
        if (user == null) {
            result.put("msg", "用户不存在");
        } else if (!user.getUserPassword().equals(SecureUtil.md5(password))) {
            result.put("msg", "密码错误");
        } else {
            String token = JWT.sign(name, SecureUtil.md5(password), 5 * 60);
            if (redisOpen) {
                redisUtil.create(user.getUuid(), token);
            } else {
                cacheManager.getCache("user").put(user.getUuid(), token);
            }
            result.put("msg", "登录成功");
            result.put("token", token);
        }
        return result;
    }

    /* fixme 注销（JWT的注销需要登录后才能访问） */
    @GetMapping("/doLogout")
    public Map doLogout() {
        Subject subject = SecurityUtils.getSubject();
        String name = JWT.getName((String) subject.getPrincipal());
        User user = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getUserName, name));

        Map result = new HashMap();
        if (user != null) {
            if (redisOpen) {
                redisUtil.delete(user.getUuid());
            } else {
                cacheManager.getCache("user").put(user.getUuid(), "");
            }
            result.put("msg", "注销成功");
        } else {
            result.put("msg", "注销失败");
        }
        return result;
    }

}
