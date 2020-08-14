package com.lanxin.Exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor   //无参构造方法
@AllArgsConstructor
public class MyResult{

    private Integer code;

    private String manage; //错误返回的信息

    private Object object;  //返回的对象


    //成功的话返回的对象
    public static MyResult success(Object object) {
        return new MyResult(200,"操作成功",object);
    }
    //失败返回的对象
    public static MyResult file(Object object){
        return new MyResult(101,"操作失败",object);
    }

}
