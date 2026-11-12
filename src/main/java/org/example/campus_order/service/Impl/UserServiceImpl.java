package org.example.campus_order.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.campus_order.entity.User;
import org.example.campus_order.mapper.UserMapper;
import org.example.campus_order.service.UserService;
import org.example.campus_order.utils.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getSelfUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 脱敏，不返回密码
        user.setPassword(null);
        return user;
    }

    @Override
    public String updateSelfUserInfo(User paramUser, Long userId) {
        User old = getById(userId);
        if (old == null) {
            throw new BusinessException("用户不存在");
        }
        // 只允许修改用户名、手机号、头像，禁止修改密码、角色
        if (paramUser.getUsername() != null) old.setUsername(paramUser.getUsername());
        if (paramUser.getPhone() != null) old.setPhone(paramUser.getPhone());
        if (paramUser.getAvatar() != null) old.setAvatar(paramUser.getAvatar());
        updateById(old);
        return "修改成功";
    }
}
