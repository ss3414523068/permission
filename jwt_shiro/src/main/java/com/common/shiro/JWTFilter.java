package com.common.shiro;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JWTFilter extends BasicHttpAuthenticationFilter {

    /*
     * ①判断是否需要登录，如果不需要直接访问
     * ②登录成功放行，登录失败重定向
     * */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginAttempt(request, response)) {
            try {
                executeLogin(request, response);
            } catch (Exception e) {
                String message = e.getMessage();
                if ("用户不存在".equals(message)) {
                    redirect("/login/exist", response);
                } else if ("密码错误".equals(message)) {
                    redirect("/login/error", response);
                } else {
                    /* token无效 */
                    redirect("/login/invalid", response);
                }
            }
        }
        return true;
    }

    /* 根据Header中的token字段判断是否需要登录 */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("token");
        return token != null;
    }

    /* 调用Realm.doGetAuthenticationInfo方法登录 */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String token = httpServletRequest.getHeader("token");
        JWTToken jwtToken = new JWTToken(token);
        getSubject(request, response).login(jwtToken);
        return true;
    }

    /* 浏览器重定向 */
    private void redirect(String redirect, ServletResponse response) {
        try {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.sendRedirect(redirect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
