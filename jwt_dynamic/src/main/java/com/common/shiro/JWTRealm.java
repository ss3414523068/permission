package com.common.shiro;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.common.util.RedisUtil;
import com.module.demo.mapper.PermissionMapper;
import com.module.demo.mapper.RoleMapper;
import com.module.demo.mapper.UserMapper;
import com.module.demo.model.Permission;
import com.module.demo.model.Role;
import com.module.demo.model.User;
import javautil.security.JWT;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JWTRealm extends AuthorizingRealm {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermissionMapper permissionMapper;

    @Value("${redis.open}")
    private Boolean redisOpen;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CacheManager cacheManager;

    /*
     * ①必须重写此方法，否则报错
     * ②此处将AuthenticationToken转换为JWTToken
     * */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /* 登录 */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String token = (String) authenticationToken.getCredentials(); /* 此处token由JWTFilter获取 */
        String name = JWT.getName(token);
        if (name == null) {
            throw new AuthenticationException("token无效");
        } else {
            User user = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getUserName, name));
            if (user == null) {
                throw new AuthenticationException("用户不存在");
            } else if (!JWT.verify(token, name, user.getUserPassword())) {
                throw new AuthenticationException("密码错误");
            } else {
                String cacheToken = "";
                if (redisOpen) {
                    cacheToken = redisUtil.read(user.getUuid());
                } else {
                    cacheToken = cacheManager.getCache("user").get(user.getUuid(), String.class);
                }
                if (!token.equals(cacheToken)) {
                    throw new AuthenticationException("token无效");
                }
            }
        }
        return new SimpleAuthenticationInfo(token, token, "JWTRealm");
    }

    /* 授权 */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String token = (String) principalCollection.getPrimaryPrincipal();
        String name = JWT.getName(token);
        if (name == null) {
            throw new AuthenticationException("token无效");
        } else {
            User user = userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getUserName, name));
            if (user == null) {
                throw new AuthenticationException("用户不存在");
            } else if (!JWT.verify(token, name, user.getUserPassword())) {
                throw new AuthenticationException("密码错误");
            }
        }

        User select = new User();
        select.setUserName(name);
        List<Role> roleList = roleMapper.selectRoleList(select);
        Set<String> stringRoles = new HashSet<>();
        for (int i = 0; i < roleList.size(); i++) {
            stringRoles.add(roleList.get(i).getRoleName());
        }
        List<Permission> permissionList = permissionMapper.selectPermissionList(select);
        Set<String> stringPermissions = new HashSet<>();
        for (int i = 0; i < permissionList.size(); i++) {
            stringPermissions.add(permissionList.get(i).getPermissionPerm());
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(stringRoles);
        info.setStringPermissions(stringPermissions);
        return info;
    }

}
