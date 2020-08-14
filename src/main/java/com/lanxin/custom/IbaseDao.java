package com.lanxin.custom;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;

import java.util.Set;

@Mapper
public interface IbaseDao {

    //根据用户名查密码
    public String findpassword(String username);

    //根据用户名查角色
    public Set<String> findRoles(String username);

    //根据用户名查权限
    public Set<String> findfunction(String username);

}
