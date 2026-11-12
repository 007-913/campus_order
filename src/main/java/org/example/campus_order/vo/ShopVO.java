package org.example.campus_order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 店铺视图对象
 * 用于返回店铺信息给前端，不包含密码等敏感字段
 */
@Data
public class ShopVO {

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

    /**
     * 状态：1=正常，2=封禁
     */
    private Integer status;

    /**
     * 营业状态：1=营业，0=打烊
     */
    private Integer businessStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 已完成订单数，用于用户端真实热销榜
     */
    private Integer sales;

    /**
     * 已完成订单平均金额
     */
    private BigDecimal avgPrice;

    /**
     * 已完成订单总金额
     */
    private BigDecimal completedAmount;
}
