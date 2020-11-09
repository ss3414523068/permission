package com.module.demo.controller.back;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.module.demo.mapper.UserMapper;
import com.module.demo.mapper.UserRoleMapper;
import com.module.demo.model.User;
import com.module.demo.model.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String userName) {
        Page<User> userPage = new Page<>();
        userPage.setCurrent(currentPage);
        userPage.setSize(pageSize);
        IPage<User> userList = new Page<>();
        if (userName != null && !userName.isEmpty()) {
            userList = userMapper.selectPage(userPage, new QueryWrapper<User>().lambda().eq(User::getUserName, userName));
        } else {
            userList = userMapper.selectPage(userPage, new QueryWrapper<User>());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("msg", "用户列表信息");
        Map data = new LinkedHashMap();
        data.put("userList", userList.getRecords());
        result.put("data", data);
        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> create(User user) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            User exist = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getUserName, user.getUserName()));
            if (exist != null) {
                result.put("msg", "用户已被创建");
            } else {
                user.setUuid(UUID.randomUUID().toString());
                user.setUserPassword(SecureUtil.md5(user.getUserPassword()));
                userMapper.insert(user);
                result.put("msg", "用户创建成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "用户创建失败");
        }
        return result;
    }

    @GetMapping("/get")
    public Map<String, Object> get(Integer id) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            result.put("msg", "用户获取成功");
            Map data = new LinkedHashMap();
            data.put("user", userMapper.selectById(id));
            result.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "用户获取失败");
        }
        return result;
    }

    @PostMapping("/update")
    public Map<String, Object> update(User user) {
        if (user.getUserPassword() != null) {
            user.setUserPassword(SecureUtil.md5(user.getUserPassword()));
        }
        /* 不允许修改userName */
        user.setUserName(null);

        Map<String, Object> result = new LinkedHashMap<>();
        try {
            userMapper.updateById(user);
            result.put("msg", "用户更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "用户更新失败");
        }
        return result;
    }

    @GetMapping("/delete")
    public Map<String, Object> delete(Integer id) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            userMapper.deleteById(id);
            userRoleMapper.delete(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUserId, id));
            result.put("msg", "用户删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "用户删除失败");
        }
        return result;
    }

}
