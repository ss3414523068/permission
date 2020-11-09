package com.module.demo.controller.back;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.module.demo.mapper.RoleMapper;
import com.module.demo.mapper.RolePermissionMapper;
import com.module.demo.mapper.UserRoleMapper;
import com.module.demo.model.Role;
import com.module.demo.model.RolePermission;
import com.module.demo.model.UserRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Api(description = "角色管理")
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @ApiOperation("角色列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小"),
            @ApiImplicitParam(name = "roleName", value = "角色名"),
    })
    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String roleName) {
        Page<Role> rolePage = new Page<>();
        rolePage.setCurrent(currentPage);
        rolePage.setSize(pageSize);
        IPage<Role> roleList = new Page<>();
        if (roleName != null && !roleName.isEmpty()) {
            roleList = roleMapper.selectPage(rolePage, new QueryWrapper<Role>().lambda().eq(Role::getRoleName, roleName));
        } else {
            roleList = roleMapper.selectPage(rolePage, new QueryWrapper<Role>());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("msg", "角色列表信息");
        Map data = new LinkedHashMap();
        data.put("roleList", roleList.getRecords());
        result.put("data", data);
        return result;
    }

    @ApiOperation("创建角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleName", value = "角色名", required = true),
    })
    @PostMapping("/create")
    public Map<String, Object> create(Role role) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            roleMapper.insert(role);
            result.put("msg", "角色创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "角色创建失败");
        }
        return result;
    }

    @ApiOperation("获取角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "角色Id", required = true),
    })
    @GetMapping("/get")
    public Map<String, Object> get(Integer id) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            result.put("msg", "角色获取成功");
            Map data = new LinkedHashMap();
            data.put("role", roleMapper.selectById(id));
            result.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "角色获取失败");
        }
        return result;
    }

    @ApiOperation("更新角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "角色Id", required = true),
            @ApiImplicitParam(name = "roleName", value = "角色名"),
    })
    @PostMapping("/update")
    public Map<String, Object> update(Role role) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            roleMapper.updateById(role);
            result.put("msg", "角色更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "角色更新失败");
        }
        return result;
    }

    /* 正在使用的角色无法删除+同时删除角色-权限关联信息 */
    @ApiOperation("删除角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "角色Id", required = true),
    })
    @GetMapping("/delete")
    public Map<String, Object> delete(Integer id) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<UserRole> userRoleList = userRoleMapper.selectList(new QueryWrapper<UserRole>().lambda().eq(UserRole::getRoleId, id));
        if (userRoleList.size() > 0) {
            result.put("msg", "角色删除失败，正在使用的角色无法删除");
        } else {
            try {
                roleMapper.deleteById(id);
                rolePermissionMapper.delete(new QueryWrapper<RolePermission>().lambda().eq(RolePermission::getRoleId, id));
                result.put("msg", "角色删除成功");
            } catch (Exception e) {
                e.printStackTrace();
                result.put("msg", "角色删除失败");
            }
        }
        return result;
    }

}
