package org.example.campus_order.vo;

import lombok.Data;

/**
 * 登录视图对象
 * 用于返回登录成功后的信息给前端
 */
@Data
public class LoginVO {

    /**
     * JWT令牌
     */
    private String token;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色：0=管理员, 1=商家, 2=学生
     */
    private Integer role;

    /**
     * 用户名/店铺名
     */
    private String username;

    /**
     * 店铺ID（商家登录时返回）
     */
    private Long shopId;
}