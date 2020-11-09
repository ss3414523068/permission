package com.interceptor;

import com.mapper.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MyInterceptor implements HandlerInterceptor {

    @Autowired
    private PermissionMapper permissionMapper;

    /*
     * （下一个拦截器/Controller）处理请求前调用，第三个参数o为（下一个拦截器/Controller）
     * 返回true继续流程（下一个拦截器/Controller）
     * 返回false中断流程，不会调用下一个拦截器/Controller（此时需要通过response告知用户）
     * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {

        /*
         * Cookie/Session
         * ①Shiro项目重启服务器后，根据Cookie中的信息依旧处于登录状态
         * ②Cookie/Session项目重启服务器后，Session清空，需要重新登录
         * （根据Cookie中的信息构造登录信息）
         * ③request只能获取同域Cookie
         * */
        Object session = request.getSession().getAttribute("user");
        if (session == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (Cookie cookie : cookies) {
//                    System.out.println(cookie.getName() + ":" + cookie.getValue());
                }
            }
        }

        /*
         * ①不向客户端写入任何Cookie
         * （Interceptor可以拦截/WEB-INF/jsp目录下的所有路由，但是位于/WEB-INF目录下的无法拦截，JSP中相互引入也会写入JSESSIONID）
         * （在Controller方法中引入response也会写入JSESSIONID）
         * （Servlet返回验证码也会写入）
         * ②两个项目，IP相同端口不同，同名Cookie会互相覆盖
         * （一个项目禁用Cookie，通过URL回写sessionId）
         * ③分布式部署不一定需要引入Redis，两个项目同时维持一个非JSESSIONID Cookie即可（需要在同一个域名下，用Nginx反向代理）
         * （Cookie储存在Redis中，避免项目重启失效）
         * */
        response.setHeader("Set-Cookie", "");
        String method = request.getMethod();
        if ("GET".equals(method)) {
            String param = request.getQueryString();
            if (param != null && param.contains("jsessionid")) {
                return true;
            } else if (param != null) {
                String url = request.getRequestURL() + param + "?jsessionid=" + request.getSession().getId();
                response.sendRedirect(url);
                return false;
            } else {
                String url = request.getRequestURL() + "?jsessionid=" + request.getSession().getId();
                response.sendRedirect(url);
                return false;
            }
        } else return "POST".equals(method);

//        /*
//         * 判断要访问的URL属于前台/后台
//         * ①根据Controller的Mapping
//         * */
//        Map<String, Integer> map = new HashMap();
//        map.put("login", 0);
//        map.put("home", 0);
//        map.put("frames", 0);
//        map.put("model", 1);
//        map.put("back", 2);
//
//        String[] URIArray = request.getRequestURI().split("/");
//        String controller = URIArray[1];
////        String method = URIArray[2];
//
//        int flag = 0;
//        for (Map.Entry<String, Integer> entry : map.entrySet()) {
//            if (entry.getKey().equals(controller)) {
//                flag = entry.getValue();
//                break;
//            }
//        }
//
//        /*
//         * 根据flag判断是否需要登录/权限
//         * ①0/default：公共，直接跳转
//         * ②1：前台
//         * ③2：后台，需要登录/权限
//         * */
//        switch (flag) {
//            case 0:
//                return true;
//            case 1:
//            case 2:
//                User user = (User) request.getSession().getAttribute("user"); /* 暂不考虑多线程问题，用户每次访问后台都会查询其权限 */
//                if (user != null) {
//                    /* 细粒度权限控制，精确到具体方法 */
//                    List<Permission> permissionList = permissionMapper.selectPermissionList(user);
//                    for (Permission permission : permissionList) {
//                        if (permission.getPermissionUrl().equals(request.getRequestURI())) {
//                            request.getSession().setAttribute("sessionUser", user);
//                            return true;
//                        }
//                    }
//                    return returnFalse(response); /* 没有权限 */
//                }
//                return returnFalse(response); /* 未登录 */
//            default:
//                return true;
//        }

    }

    /*
     * （上一个拦截器/Controller）处理请求后调用
     * 第三个参数o为（下一个拦截器/Controller），modelAndView为上一个处理后的结果
     * */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {

    }

    /*
     * 在DispatcherServlet完全处理完，即视图渲染完后调用
     * */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) throws Exception {

    }

    /************************************************************分割线************************************************************/

    /*
     * 未登录/没有权限
     * ①中断流程，直接跳转至未登录页面
     * */
    private boolean returnFalse(HttpServletResponse response) throws IOException {
        response.sendRedirect("/login/noPermission");
        return true;
    }

}
