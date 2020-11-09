package com.common.shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ShiroInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String para1 = request.getParameter("para1");
        String para2 = request.getParameter("para2");
        System.out.println(para1 + para2);

        /* 手动构造登录信息 */
        UsernamePasswordToken token = new UsernamePasswordToken("logged", "123456");
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            /* fixme 拦截器中重定向会出现response重复提交错误 */
            return redirect(response, "/back/home");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return redirect(response, "/login/loginFailure");
    }

    /* 向浏览器发送重定向信息 */
    private boolean redirect(HttpServletResponse response, String url) throws IOException {
        response.sendRedirect(url);
        return true;
    }

}
