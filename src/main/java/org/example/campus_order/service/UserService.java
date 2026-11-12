package org.example.campus_order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.campus_order.entity.User;

public interface UserService extends IService<User> {

    /**
     * 查询当前登录用户信息，脱敏隐藏密码
     */
    User getSelfUserInfo(Long userId);

    /**
     * 更新用户基础信息，禁止修改密码、角色
     */
    String updateSelfUserInfo(User paramUser, Long userId);
}