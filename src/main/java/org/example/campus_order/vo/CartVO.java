package org.example.campus_order.vo;

import lombok.Data;

/**
 * 购物车视图对象
 * 用于返回购物车信息给前端，包含菜品详情
 */
@Data
public class CartVO {

    /**
     * 购物车项ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 店铺名称（关联查询）
     */
    private String shopName;

    /**
     * 菜品ID
     */
    private Long foodId;

    /**
     * 菜品名称（关联查询）
     */
    private String foodName;

    /**
     * 菜品图片（关联查询）
     */
    private String foodImage;

    /**
     * 菜品价格（关联查询）
     */
    private Double foodPrice;

    /**
     * 数量
     */
    private Integer num;

    /**
     * 小计
     */
    private Double subtotal;
}