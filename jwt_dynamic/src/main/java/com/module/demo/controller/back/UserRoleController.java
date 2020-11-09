package com.module.demo.controller.back;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.module.demo.mapper.UserRoleMapper;
import com.module.demo.model.UserRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Api(description = "用户角色关联管理")
@RestController
@RequestMapping("/userRole")
public class UserRoleController {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @ApiOperation("创建用户角色关联")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true),
            @ApiImplicitParam(name = "roleId", value = "角色Id", required = true),
    })
    @PostMapping("/create")
    public Map<String, Object> create(UserRole userRole) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            userRoleMapper.insert(userRole);
            result.put("msg", "用户角色关联创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "用户角色关联创建失败");
        }
        return result;
    }

    @ApiOperation("删除用户角色关联")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true),
    })
    @GetMapping("/delete")
    public Map<String, Object> delete(Integer userId) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            userRoleMapper.delete(new QueryWrapper<UserRole>().lambda().eq(UserRole::getUserId, userId));
            result.put("msg", "用户角色关联删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "用户角色关联删除失败");
        }
        return result;
    }

}
