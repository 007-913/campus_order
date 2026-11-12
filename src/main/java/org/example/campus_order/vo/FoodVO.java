package org.example.campus_order.vo;

import lombok.Data;

/**
 * 菜品视图对象
 * 用于返回菜品信息给前端，包含关联的分类名称
 */
@Data
public class FoodVO {

    /**
     * 菜品ID
     */
    private Long id;

    /**
     * 所属店铺ID
     */
    private Long shopId;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称（关联查询）
     */
    private String categoryName;

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
     * 状态：1=上架，0=下架
     */
    private Integer status;

    /**
     * 描述
     */
    private String description;
}