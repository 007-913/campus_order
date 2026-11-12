package org.example.campus_order.dto;

import lombok.Data;

/**
 * 菜品数据传输对象
 * 用于商家添加和更新菜品
 */
@Data
public class FoodDTO {

    /**
     * 菜品ID（更新时需要）
     */
    private Long id;

    /**
     * 所属店铺ID（后端自动填充）
     */
    private Long shopId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 菜品名称
     */
    private String foodName;

    /**
     * 价格
     */
    private Double price;

    /**
     * 图片
     */
    private String image;

    /**
     * 描述
     */
    private String description;

    /**
     * 状态：1=上架，0=下架
     */
    private Integer status;
}