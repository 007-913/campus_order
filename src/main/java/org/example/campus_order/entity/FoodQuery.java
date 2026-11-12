package org.example.campus_order.entity;

import lombok.Data;

@Data
public class FoodQuery {
    // 菜品名称模糊搜索
    private String foodName;
    // 分类id筛选
    private Long categoryId;
    // 上下架状态筛选 1上架 0下架
    private Integer status;
}