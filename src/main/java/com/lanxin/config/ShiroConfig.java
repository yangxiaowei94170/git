package com.lanxin.config;

import com.lanxin.custom.Custom;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(10);
        return hashedCredentialsMatcher;
    }

    @Bean
    public Custom custom(HashedCredentialsMatcher hashedCredentialsMatcher) {
        Custom custom = new Custom();
        custom.setCredentialsMatcher(hashedCredentialsMatcher);
        return custom;
    }

    @Bean
    public SecurityManager securityManager(Custom custom) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置realm
        securityManager.setRealm(custom);
        //自定义缓存实现
        securityManager.setCacheManager(cacheManager());
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    //过滤器
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, String> filterChainmap = new LinkedHashMap<>();
        filterChainmap.put("/logout", "anon");
        filterChainmap.put("/login", "anon");
        //当没有登录的时候访问别的路径
        filterChainmap.put("/**", "authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainmap);
        shiroFilterFactoryBean.setLoginUrl("/unauth");

        return shiroFilterFactoryBean;
    }

    //controller开启权限验证注解后 ，要把注解交个springioc容器不然没作用
    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator =
                new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    //使用代理方式，所有需要开启代理支持
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
            SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor aas = new AuthorizationAttributeSourceAdvisor();
        aas.setSecurityManager(securityManager);
        return aas;
    }

    //----------全局session管理（集群环境）
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;

    //配置shiro-redisManager   要使用shiro-redis开源插件
    @Bean
    public RedisManager redisManager(){
        RedisManager redisManager=new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        redisManager.setExpire(1800);
        return redisManager;
    }

    @Bean
    public RedisSessionDAO redisSessionDAO(){
        RedisSessionDAO redisSessionDAO=new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    @Bean
    public DefaultWebSessionManager sessionManager(){
        DefaultWebSessionManager sessionManager=new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        return sessionManager;
    }

    //开启shiro-reids支持
    @Bean
    public RedisCacheManager cacheManager(){
        RedisCacheManager redisCacheManager=new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

}
