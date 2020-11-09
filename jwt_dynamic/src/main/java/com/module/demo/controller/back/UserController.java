package com.module.demo.controller.back;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.module.demo.mapper.UserMapper;
import com.module.demo.mapper.UserRoleMapper;
import com.module.demo.model.User;
import com.module.demo.model.UserRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Api(description = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @ApiOperation("用户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小"),
            @ApiImplicitParam(name = "userName", value = "用户名"),
    })
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

    @ApiOperation("创建用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userName", value = "用户名", required = true),
            @ApiImplicitParam(name = "userPassword", value = "用户密码", required = true),
    })
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

    @ApiOperation("获取用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户Id", required = true),
    })
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

    @ApiOperation("更新用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户Id", required = true),
            @ApiImplicitParam(name = "userPassword", value = "用户密码"),
    })
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

    /* 删除用户的同时删除用户-角色关联信息 */
    @ApiOperation("删除用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户Id", required = true),
    })
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
