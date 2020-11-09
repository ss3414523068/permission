package com.common.config;

import com.common.shiro.ShiroInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /*
     * 配置拦截器
     * （过滤器可以通过FilterRegistrationBean单独配置）
     * */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ShiroInterceptor())
                .addPathPatterns("/interceptor/**")
                .excludePathPatterns("/"); /* 排除/路由 */
    }

}
