package com.lanxin.custom;

import org.apache.ibatis.session.SqlSession;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

//自定义Realm
public class Custom extends AuthorizingRealm {

    @Resource
    private IbaseDao dao;
    //封装认证信息
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //拿到传进来的账号
        String username =(String) authenticationToken.getPrincipal();
        //调用查询密码的方法
        String password=dao.findpassword(username);
        //如果密码不为null把查询出来的账号密码放到验证缓存中，然后在判断传入的密码是否正确
        if(password!=null){
            SimpleAuthenticationInfo simpleAuthenticationInfo    //realmName  是随便取的名字
                    =new SimpleAuthenticationInfo(username,password,"coustomRealm");
            //加盐
            simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(username+"!@#$%^&*"));
            return simpleAuthenticationInfo;
        }
        return null;
    }

    //封装授权信息
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username=(String) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo simpleAuthorizationInfo=new SimpleAuthorizationInfo();
        //查询角色信息并设置
        Set<String> roles = dao.findRoles(username);
        simpleAuthorizationInfo.setRoles(roles);
        //查询授权信息并设置
        Set<String> findfunction = dao.findfunction(username);
        simpleAuthorizationInfo.setStringPermissions(findfunction);
        return simpleAuthorizationInfo;
    }

    //验证
    public static void main(String[] args) {
        Custom custom=new Custom();
        DefaultSecurityManager defaultSecurityManager=new DefaultSecurityManager();
        defaultSecurityManager.setRealm(custom);

        HashedCredentialsMatcher hc=new HashedCredentialsMatcher();
        hc.setHashAlgorithmName("md5");
        //设置加密次数
        hc.setHashIterations(10);
        custom.setCredentialsMatcher(hc);
        //主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject=SecurityUtils.getSubject();

        UsernamePasswordToken token=new UsernamePasswordToken("admin","beans123");
        subject.login(token);

        System.out.println("111111111111");
        System.out.println(subject.isAuthenticated());
        //验证角色
        subject.checkRole("admin");
        //验证权限
        subject.checkPermissions("select");

    }

}
