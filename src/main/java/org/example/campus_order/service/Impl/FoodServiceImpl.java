package org.example.campus_order.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.campus_order.entity.Food;
import org.example.campus_order.entity.FoodQuery;
import org.example.campus_order.mapper.FoodMapper;
import org.example.campus_order.service.FoodService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FoodServiceImpl extends ServiceImpl<FoodMapper, Food> implements FoodService {

    @Override
    public List<Food> getShopFoodList(Long shopId, FoodQuery query) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Food::getShopId, shopId);

        if (query != null && query.getFoodName() != null && !query.getFoodName().trim().isEmpty()) {
            wrapper.like(Food::getFoodName, query.getFoodName().trim());
        }
        if (query != null && query.getCategoryId() != null) {
            wrapper.eq(Food::getCategoryId, query.getCategoryId());
        }
        if (query != null && query.getStatus() != null) {
            wrapper.eq(Food::getStatus, query.getStatus());
        }
        return list(wrapper);
    }

    @Override
    public String addFood(Food food, Long shopId) {
        food.setShopId(shopId);
        if (food.getStatus() == null) {
            food.setStatus(1);
        }
        save(food);
        return "添加成功";
    }

    @Override
    public String updateFood(Food food, Long shopId) {
        Food existing = getById(food.getId());
        if (existing == null) {
            throw new RuntimeException("菜品不存在");
        }
        if (!existing.getShopId().equals(shopId)) {
            throw new RuntimeException("无权操作他人店铺菜品");
        }
        food.setShopId(shopId);
        updateById(food);
        return "修改成功";
    }

    @Override
    public String deleteFood(Long foodId, Long shopId) {
        Food existing = getById(foodId);
        if (existing == null) {
            throw new RuntimeException("菜品不存在");
        }
        if (!existing.getShopId().equals(shopId)) {
            throw new RuntimeException("无权操作他人店铺菜品");
        }
        removeById(foodId);
        return "删除成功";
    }

    @Override
    public String toggleFoodStatus(Long foodId, Long shopId) {
        Food existing = getById(foodId);
        if (existing == null) {
            throw new RuntimeException("菜品不存在");
        }
        if (!existing.getShopId().equals(shopId)) {
            throw new RuntimeException("无权操作他人店铺菜品");
        }
        existing.setStatus(existing.getStatus() == 1 ? 0 : 1);
        updateById(existing);
        return existing.getStatus() == 1 ? "已上架" : "已下架";
    }

    @Override
    public List<Food> getOpenShopFood(Long shopId) {
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Food::getShopId, shopId)
                .eq(Food::getStatus, 1);
        return list(wrapper);
    }
}