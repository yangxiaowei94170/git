package com.lanxin.Exception;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice   //全局异常捕捉
public class AllResult {

    MyException myException;
    MyResult myResult;
    @ExceptionHandler(value = Exception.class)
    //ex是扑捉到的异常
    public MyResult errorHandler(Exception ex){

        if(ex instanceof MyException){
            myException=(MyException)ex;
            myResult =new MyResult(myException.getCode(),myException.getManage(),null);
            return myResult;
        }
        return MyResult.file(null);
    }

    //当访问没有权限的方法时会抛出下面这个异常，然后直接捕捉
    @ResponseBody
    @ExceptionHandler(value = AuthorizationException.class)
    public MyResult Authoriztion(){
        return MyResult.file("没有权限，请联系管理员");
    }
}
