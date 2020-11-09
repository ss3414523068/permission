package com.module.demo.controller.back;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.module.demo.mapper.RouteMapper;
import com.module.demo.model.Route;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Api(description = "路由管理")
@RestController
@RequestMapping("/route")
public class RouteController {

    @Autowired
    private RouteMapper routeMapper;

    @ApiOperation("路由列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "currentPage", value = "当前页"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小"),
            @ApiImplicitParam(name = "routeName", value = "路由名"),
    })
    @GetMapping("/list")
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String routeName) {
        Page<Route> routePage = new Page<>();
        routePage.setCurrent(currentPage);
        routePage.setSize(pageSize);
        IPage<Route> routeList = new Page<>();
        if (routeName != null && !routeName.isEmpty()) {
            routeList = routeMapper.selectPage(routePage, new QueryWrapper<Route>().lambda().eq(Route::getRouteName, routeName));
        } else {
            routeList = routeMapper.selectPage(routePage, new QueryWrapper<Route>());
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("msg", "路由列表信息");
        Map data = new LinkedHashMap();
        data.put("routeList", routeList.getRecords());
        result.put("data", data);
        return result;
    }

    @ApiOperation("创建路由")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "routeSort", value = "路由排序"),
            @ApiImplicitParam(name = "routeName", value = "路由名", required = true),
            @ApiImplicitParam(name = "routeUrl", value = "路由路径", required = true),
            @ApiImplicitParam(name = "routePerm", value = "路由权限", required = true),
    })
    @PostMapping("/create")
    public Map<String, Object> create(Route route) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            Route exist = routeMapper.selectOne(new QueryWrapper<Route>().lambda().eq(Route::getRouteUrl, route.getRouteUrl()));
            if (exist != null) {
                result.put("msg", "路由已被创建");
            } else {
                route.setRoutePerm("perms[" + route.getRoutePerm() + "]");
                routeMapper.insert(route);
                result.put("msg", "路由创建成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "路由创建失败");
        }
        return result;
    }

    @ApiOperation("获取路由")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "路由Id", required = true),
    })
    @GetMapping("/get")
    public Map<String, Object> get(Integer id) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            result.put("msg", "路由获取成功");
            Map data = new LinkedHashMap();
            data.put("route", routeMapper.selectById(id));
            result.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "路由获取失败");
        }
        return result;
    }

    /* 同一个路由不能有多种权限 */
    @ApiOperation("更新路由")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "路由Id", required = true),
            @ApiImplicitParam(name = "routeSort", value = "路由排序"),
            @ApiImplicitParam(name = "routeName", value = "路由名"),
            @ApiImplicitParam(name = "routePerm", value = "路由权限", required = true),
    })
    @PostMapping("/update")
    public Map<String, Object> update(Route route) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            route.setRouteUrl(null);
            routeMapper.updateById(route);
            result.put("msg", "路由更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "路由更新失败");
        }
        return result;
    }

    @ApiOperation("删除路由")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "路由Id", required = true),
    })
    @GetMapping("/delete")
    public Map<String, Object> delete(Integer id) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            routeMapper.deleteById(id);
            result.put("msg", "路由删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "路由删除失败");
        }
        return result;
    }

}
