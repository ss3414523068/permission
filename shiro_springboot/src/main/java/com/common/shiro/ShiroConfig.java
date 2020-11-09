package com.common.shiro;

import javautil.sql.JDBC;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Configuration
public class ShiroConfig {

    private JDBC util;

    public ShiroConfig() throws IOException {
        InputStream inputStream = ShiroConfig.class.getResourceAsStream("/application.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        String url = properties.getProperty("spring.datasource.druid.url");
        String username = properties.getProperty("spring.datasource.druid.username");
        String password = properties.getProperty("spring.datasource.druid.password");
        util = new JDBC(url, username, password);
    }

    /* MD5加密，加密1次，使用Hex存储 */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(1);
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return hashedCredentialsMatcher;
    }

    /* 自定义Realm */
    @Bean
    public RBACRealm RBACRealm() {
        RBACRealm myRealm = new RBACRealm();
        myRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return myRealm;
    }

    /* SecurityManager */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setRealm(RBACRealm());
        return defaultWebSecurityManager;
    }

    /* ShiroFilter */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        /* 登录/登录成功/未授权URL */
        shiroFilterFactoryBean.setLoginUrl("/login/login");
        shiroFilterFactoryBean.setUnauthorizedUrl("/login/noPermission"); /* 生效 */
        /*
         * shiroFilter拦截链
         * ①路由写在2个拦截链中或写在1个拦截链中用逗号分割都需要用户同时拥有两种权限
         * ②角色当作权限的集合，拦截链中禁止使用角色，也禁止使用/**通配符（authc除外）
         * */
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
//        filterChainDefinitionMap.put("/", "anon");
//        filterChainDefinitionMap.put("/login/**", "anon");
//        filterChainDefinitionMap.put("/doLogout", "logout");
//        filterChainDefinitionMap.put("/**", "authc");

        /* 此处注入Mapper/JDBCTemplate均报错，只能使用JDBC */
        List<Map<String, Object>> filterList = util.select("SELECT * FROM shiro_filter order by filter_sort");
        for (int i = 0; i < filterList.size(); i++) {
            filterChainDefinitionMap.put((String) filterList.get(i).get("filter_url"), (String) filterList.get(i).get("filter_perm"));
        }

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /************************************************************分割线************************************************************/
    /*
     * ①分割线之上对应原shiro_ssm的Spring配置applicationContext.xml
     * ②分割线之下对应SpringMVC配置dispatcher-servlet.xml
     * */

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
