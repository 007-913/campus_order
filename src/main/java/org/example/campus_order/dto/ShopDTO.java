package org.example.campus_order.dto;

import lombok.Data;

/**
 * 店铺数据传输对象
 * 用于商家店铺信息更新
 */
@Data
public class ShopDTO {

    /**
     * 店铺ID
     */
    private Long id;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 商家用户名
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
     * 地址
     */
    private String address;

    /**
     * 封面图
     */
    private String cover;
}