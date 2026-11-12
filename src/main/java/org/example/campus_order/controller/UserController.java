package org.example.campus_order.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.campus_order.dto.UserDTO;
import org.example.campus_order.entity.User;
import org.example.campus_order.service.UserService;
import org.example.campus_order.utils.Result;
import org.example.campus_order.utils.RoleCheckUtil;
import org.example.campus_order.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器 - 学生维护个人信息
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 学生查看自己信息
     */
    @GetMapping("/info")
    public Result<UserVO> info(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isUser(role)) {
            return Result.error("无权限访问");
        }
        Long userId = (Long) request.getAttribute("loginId");
        User user = userService.getSelfUserInfo(userId);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return Result.success(userVO);
    }

    /**
     * 学生修改个人信息（禁止改密码和角色）
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isUser(role)) {
            return Result.error("无权限访问");
        }
        Long userId = (Long) request.getAttribute("loginId");
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        String msg = userService.updateSelfUserInfo(user, userId);
        return Result.success(msg);
    }
}
