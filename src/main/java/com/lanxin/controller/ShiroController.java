package com.lanxin.controller;

import com.lanxin.Exception.MyResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShiroController {
    @RequestMapping("/login")
    public MyResult login(String username,String password){
        Subject subject= SecurityUtils.getSubject();
        UsernamePasswordToken token=new UsernamePasswordToken(username,password);

        MyResult myResult=null;
        try {
            subject.login(token);
            myResult=MyResult.success("登录成功");
        } catch (IncorrectCredentialsException e1) {
            myResult=MyResult.file("密码错误");
        } catch (AuthenticationException e) {
            myResult=MyResult.file("用户不存在");
        }
        return myResult;
    }
    @RequestMapping("/logout")
    public MyResult logout(){

        return MyResult.file("还未登录");
    }

    @RequestMapping("/unauth")    //默认的没有登录走这个方法，路径也是默认的
    public MyResult unauth(){
        return MyResult.file("未登录");
    }

    @RequiresPermissions("select")     //权限控制
    @RequestMapping("/select")
    public MyResult select(){

        return MyResult.file("查询方法");
    }

    @RequiresPermissions("update")
    @RequestMapping("/update")
    public MyResult update(){

        return MyResult.file("修改方法");
    }
    @RequiresPermissions("delete")
    @RequestMapping("/delete")
    public MyResult delete(){

        return MyResult.file("删除方法");
    }
    @RequiresPermissions("insert")
    @RequestMapping("/insert")
    public MyResult insert(){

        return MyResult.file("添加方法");
    }

}
