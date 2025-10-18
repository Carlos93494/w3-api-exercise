package com.example.controller;

import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.utils.MyBatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController // 标识为 REST 接口控制器（自动返回 JSON）
public class UserController {

    // 1. 分页查询用户列表：GET /api/users
    @GetMapping("/users")
    public Map<String, Object> getUsers(
            @RequestParam(defaultValue = "1") Integer page, // 页码，默认1
            @RequestParam(defaultValue = "10") Integer size  // 每页条数，默认10
    ) {
        // 校验参数（每页最大50条）
        size = Math.min(size, 50);
        int startIndex = (page - 1) * size;

        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        // 分页查询
        Map<String, Integer> map = new HashMap<>();
        map.put("startIndex", startIndex);
        map.put("pageSize", size);
        List<User> users = userMapper.getUsersByPage(map);
        int total = userMapper.getTotalUserCount();

        sqlSession.close();

        // 封装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("users", users);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return result;
    }

    // 2. 新增用户：POST /api/users
    @PostMapping("/users")
    public Map<String, Object> addUser(@RequestBody User user) { // 接收 JSON 数据
        Map<String, Object> result = new HashMap<>();

        // 参数校验
        if (user.getUsername() == null || user.getUsername().length() < 2 || user.getUsername().length() > 16) {
            result.put("success", false);
            result.put("message", "用户名长度需在2-16之间");
            return result;
        }
        if (user.getPassword() == null || user.getPassword().length() < 8 || user.getPassword().length() > 20) {
            result.put("success", false);
            result.put("message", "密码长度需在8-20之间");
            return result;
        }
        if (user.getEmail() == null || !user.getEmail().matches(".+@.+\\..+")) {
            result.put("success", false);
            result.put("message", "邮箱格式不正确");
            return result;
        }

        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

        // 检查用户名是否已存在
        User existingUser = userMapper.getUserByUsername(user.getUsername());
        if (existingUser != null) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            sqlSession.close();
            return result;
        }

        // 新增用户
        int rows = userMapper.addUser(user);
        if (rows > 0) {
            result.put("success", true);
            result.put("message", "新增成功");
            result.put("user", user); // 返回新增的用户信息
        } else {
            result.put("success", false);
            result.put("message", "新增失败");
        }

        sqlSession.close();
        return result;
    }
}