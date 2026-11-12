package org.example.campus_order.dto;

import lombok.Data;

/**
 * 用户数据传输对象
 * 用于用户注册和个人信息更新
 */
@Data
public class UserDTO {

    /**
     * 用户ID（更新时需要）
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;
}