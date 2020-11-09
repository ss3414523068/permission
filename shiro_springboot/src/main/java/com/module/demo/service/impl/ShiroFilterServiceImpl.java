package com.module.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.module.demo.mapper.FilterMapper;
import com.module.demo.model.Filter;
import com.module.demo.service.ShiroFilterService;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShiroFilterServiceImpl implements ShiroFilterService {

    @Autowired
    private FilterMapper filterMapper;

    @Autowired
    private ShiroFilterFactoryBean shiroFilterFactoryBean;

    /* fixme 需要加锁保证线程同步 */
    @Override
    public void resetFilterChain() {
        try {
            AbstractShiroFilter shiroFilter = (AbstractShiroFilter) shiroFilterFactoryBean.getObject();
            PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter.getFilterChainResolver();
            DefaultFilterChainManager filterChainManager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();
            /* 清空旧权限 */
            filterChainManager.getFilterChains().clear();
            shiroFilterFactoryBean.getFilterChainDefinitionMap().clear();

            /* 生成新权限 */
            List<Filter> filterList = filterMapper.selectList(new QueryWrapper<Filter>().lambda().orderByAsc(Filter::getFilterSort));
            Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
            for (int i = 0; i < filterList.size(); i++) {
                filterChainManager.createChain(filterList.get(i).getFilterUrl(), filterList.get(i).getFilterPerm());
                filterChainDefinitionMap.put(filterList.get(i).getFilterUrl(), filterList.get(i).getFilterPerm());
            }
            shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
