package com.fuhe.youse.dao.mapper;

import com.fuhe.youse.bean.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {

    @Select(value="select username,password,enabled from users where username = #{username}")
    User loadUserByUsername(@Param("username") String username);
    @Insert(value="insert into users (username, password, enabled, create_date) value(#{username},#{password},#{enabled},#{createDate})")
    void saveUser(User user);
}
