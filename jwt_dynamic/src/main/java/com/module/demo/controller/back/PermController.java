package com.module.demo.controller.back;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.module.demo.mapper.PermissionMapper;
import com.module.demo.mapper.RolePermissionMapper;
import com.module.demo.model.Permission;
import com.module.demo.model.RolePermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Api(description = "权限管理")
@RestController
@RequestMapping("/perm")
public class PermController {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @ApiOperation("权限列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小"),
            @ApiImplicitParam(name = "permissionName", value = "权限名"),
    })
    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String permissionName) {
        Page<Permission> permissionPage = new Page<>();
        permissionPage.setCurrent(currentPage);
        permissionPage.setSize(pageSize);
        IPage<Permission> permissionList = new Page<>();
        if (permissionName != null && !permissionName.isEmpty()) {
            permissionList = permissionMapper.selectPage(permissionPage, new QueryWrapper<Permission>().lambda().eq(Permission::getPermissionName, permissionName));
        } else {
            permissionList = permissionMapper.selectPage(permissionPage, new QueryWrapper<Permission>());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("msg", "权限列表信息");
        Map data = new LinkedHashMap();
        data.put("permissionList", permissionList.getRecords());
        result.put("data", data);
        return result;
    }

    /* 每次新增权限自动附加到默认管理员角色上 */
    @ApiOperation("创建权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentId", value = "父级权限"),
            @ApiImplicitParam(name = "permissionName", value = "权限名", required = true),
            @ApiImplicitParam(name = "permissionUrl", value = "权限路由"),
            @ApiImplicitParam(name = "permissionPerm", value = "权限值", required = true),
    })
    @PostMapping("/create")
    public Map<String, Object> create(Permission permission) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            permissionMapper.insert(permission);
            rolePermissionMapper.insert(new RolePermission().setRoleId(1).setPermissionId(permission.getId())); /* 角色1为管理员角色 */
            result.put("msg", "权限创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "权限创建失败");
        }
        return result;
    }

    @ApiOperation("获取权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "权限Id", required = true),
    })
    @GetMapping("/get")
    public Map<String, Object> get(Integer id) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            result.put("msg", "权限获取成功");
            Map data = new LinkedHashMap();
            data.put("permission", permissionMapper.selectById(id));
            result.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "权限获取失败");
        }
        return result;
    }

    @ApiOperation("更新权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "权限Id", required = true),
            @ApiImplicitParam(name = "parentId", value = "父级权限"),
            @ApiImplicitParam(name = "permissionName", value = "权限名"),
            @ApiImplicitParam(name = "permissionUrl", value = "权限路由"),
            @ApiImplicitParam(name = "permissionPerm", value = "权限值"),
    })
    @PostMapping("/update")
    public Map<String, Object> update(Permission permission) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            permissionMapper.updateById(permission);
            result.put("msg", "权限更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "权限更新失败");
        }
        return result;
    }

    /* 正在使用的权限无法删除+同时删除角色-权限关联信息 */
    @ApiOperation("删除权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "权限Id", required = true),
    })
    @GetMapping("/delete")
    public Map<String, Object> delete(Integer id) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<RolePermission> rolePermissionList = rolePermissionMapper.selectList(new QueryWrapper<RolePermission>().lambda()
                .eq(RolePermission::getPermissionId, id)
                .ne(RolePermission::getRoleId, 1) /* 不包括默认管理员角色 */
        );
        if (rolePermissionList.size() > 0) {
            result.put("msg", "权限删除失败，正在使用的权限无法删除");
        } else {
            try {
                permissionMapper.deleteById(id);
                rolePermissionMapper.delete(new QueryWrapper<RolePermission>().lambda().eq(RolePermission::getPermissionId, id));
                result.put("msg", "权限删除成功");
            } catch (Exception e) {
                e.printStackTrace();
                result.put("msg", "权限删除失败");
            }
        }
        return result;
    }

}
