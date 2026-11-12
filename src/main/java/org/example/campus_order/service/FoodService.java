package org.example.campus_order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.campus_order.entity.Food;
import org.example.campus_order.entity.FoodQuery;
import java.util.List;

public interface FoodService extends IService<Food> {

    /**
     * 商家查询本店菜品（支持名称/分类/状态筛选）
     */
    List<Food> getShopFoodList(Long shopId, FoodQuery query);

    /**
     * 商家新增菜品
     */
    String addFood(Food food, Long shopId);

    /**
     * 商家修改菜品（校验店铺归属）
     */
    String updateFood(Food food, Long shopId);

    /**
     * 商家删除菜品（校验店铺归属）
     */
    String deleteFood(Long foodId, Long shopId);

    /**
     * 商家切换菜品上下架状态
     */
    String toggleFoodStatus(Long foodId, Long shopId);

    /**
     * 学生公开查询店铺已上架菜品
     */
    List<Food> getOpenShopFood(Long shopId);
}