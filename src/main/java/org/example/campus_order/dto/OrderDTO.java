package org.example.campus_order.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单数据传输对象
 * 用于学生下单
 */
@Data
public class OrderDTO {

    /**
     * 订单ID
     */
    private String id;

    /**
     * 用户ID（后端自动填充）
     */
    private Long userId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 总价
     */
    private BigDecimal totalPrice;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 备注
     */
    private String remark;
}