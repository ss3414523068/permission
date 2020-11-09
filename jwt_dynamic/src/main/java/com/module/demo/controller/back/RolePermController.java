package com.module.demo.controller.back;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.module.demo.mapper.RolePermissionMapper;
import com.module.demo.model.RolePermission;
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

@Api(description = "角色权限关联管理")
@RestController
@RequestMapping("/rolePerm")
public class RolePermController {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @ApiOperation("创建角色权限关联")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色Id", required = true),
            @ApiImplicitParam(name = "permissionId", value = "权限Id", required = true),
    })
    @PostMapping("/create")
    public Map<String, Object> create(RolePermission rolePermission) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            rolePermissionMapper.insert(rolePermission);
            result.put("msg", "角色权限关联创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "角色权限关联创建失败");
        }
        return result;
    }

    @ApiOperation("删除角色权限关联")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色Id", required = true),
    })
    @GetMapping("/delete")
    public Map<String, Object> delete(Integer roleId) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            rolePermissionMapper.delete(new QueryWrapper<RolePermission>().lambda().eq(RolePermission::getRoleId, roleId));
            result.put("msg", "角色权限关联删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "角色权限关联删除失败");
        }
        return result;
    }

}
