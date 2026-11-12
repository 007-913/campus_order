package org.example.campus_order.vo;

import lombok.Data;

import java.util.Date;

/**
 * 用户视图对象
 * 用于返回用户信息给前端，不包含敏感字段
 */
@Data
public class UserVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色：2=学生
     */
    private Integer role;

    /**
     * 状态：1=正常，0=禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;
}