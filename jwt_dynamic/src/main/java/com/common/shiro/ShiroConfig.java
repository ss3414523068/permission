package com.common.shiro;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    /* 自定义Realm */
    @Bean
    public JWTRealm JWTRealm() {
        JWTRealm myRealm = new JWTRealm();
        return myRealm;
    }

    /* SecurityManager */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(JWTRealm());
        return defaultWebSecurityManager;
    }

    /* ShiroFilter */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() throws IOException {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        /* 自定义拦截器 */
        Map<String, Filter> filters = new HashMap<>();
        /* fixme Spring管理JWTFilter */
        filters.put("jwt", new JWTFilter());
        shiroFilterFactoryBean.setFilters(filters);
        /* 自定义拦截链 */
        Map<String, String> filterChainDefinitionMap = new HashMap<>();
        /* 静态资源 */
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        /* swagger */
        filterChainDefinitionMap.put("/v2/**", "anon");
        filterChainDefinitionMap.put("/csrf/**", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        /* 拦截所有请求 */
        filterChainDefinitionMap.put("/**", "jwt");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /* 开启Shiro AOP注解 */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /* Shiro SpringMVC配置需要 */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /*
     * ①Shiro SpringMVC配置（开启Shiro注解）
     * ②依赖于LifecycleBeanPostProcessor
     * */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

}
