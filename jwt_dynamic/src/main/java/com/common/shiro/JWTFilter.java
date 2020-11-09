package com.common.shiro;

import javautil.sql.JDBC;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JWTFilter extends BasicHttpAuthenticationFilter {

    private JDBC util;

    public JWTFilter() throws IOException {
        InputStream inputStream = JWTFilter.class.getResourceAsStream("/application.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        String url = properties.getProperty("spring.datasource.druid.url");
        String username = properties.getProperty("spring.datasource.druid.username");
        String password = properties.getProperty("spring.datasource.druid.password");
        util = new JDBC(url, username, password);
    }

    /*
     * ①先查路由表，不需要登录/需要登录/需要权限
     * ②再查用户权限
     *
     * fixme 路由匹配
     *  ①完全匹配+所有路由
     *  ②通配符
     * */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String URI = httpServletRequest.getRequestURI();

        List<Map<String, Object>> routeList = util.select("SELECT * FROM shiro_route order by route_sort");
        for (Map<String, Object> route : routeList) {
            if (route.get("route_url").equals(URI)) {
                if (route.get("route_perm").equals("anon")) { /* 不需要登录 */
                    return true;
                } else if (route.get("route_perm").equals("logout")) { /* fixme JWT注销即用户抛弃token */
                    getSubject(request, response).logout();
                    return true;
                } else if (route.get("route_perm").equals("authc")) {
                    try {
                        String token = httpServletRequest.getHeader("token");
                        JWTToken jwtToken = new JWTToken(token);
                        getSubject(request, response).login(jwtToken); /* 调用Realm.doGetAuthenticationInfo()登录 */
                    } catch (Exception e) {
                        return msg(response, "token无效");
                    }
                    return true;
                } else if (((String) route.get("route_perm")).contains("perms")) {
                    Pattern pattern = Pattern.compile("perms\\[(.*)]");
                    Matcher matcher = pattern.matcher((String) route.get("route_perm"));
                    while (matcher.find()) {
                        try {
                            /* fixme 授权前需要登录，否则会使用之前的token */
                            String token = httpServletRequest.getHeader("token");
                            JWTToken jwtToken = new JWTToken(token);
                            getSubject(request, response).login(jwtToken);
                            getSubject(request, response).checkPermission(matcher.group(1)); /* 调用Realm.doGetAuthorizationInfo()授权 */
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (e.getMessage().contains("not have permission")) {
                                return msg(response, "没有权限");
                            } else {
                                return msg(response, "token无效");
                            }
                        }
                    }
                    return true;
                }
            }
        }

        return msg(response, "不在路由表中");
    }

    /* 通过修改response发送消息 */
    private boolean msg(ServletResponse response, String msg) {
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setCharacterEncoding("UTF-8");
            PrintWriter writer = httpServletResponse.getWriter();
            writer.print("{\"msg\":\"" + msg + "\"}");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
