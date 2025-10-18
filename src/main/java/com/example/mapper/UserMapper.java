package com.example.mapper;

import com.example.entity.User;
import java.util.List;
import java.util.Map;

public interface UserMapper {
    // 分页查询用户
    List<User> getUsersByPage(Map<String, Integer> map);

    // 查询总用户数（用于分页）
    int getTotalUserCount();

    // 新增用户
    int addUser(User user);

    // 根据用户名查询（判断是否已存在）
    User getUserByUsername(String username);
}