package org.example.campus_order.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.campus_order.entity.Category;
import org.example.campus_order.mapper.CategoryMapper;
import org.example.campus_order.service.CategoryService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getShopCategoryList(Long shopId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getShopId, shopId);
        return list(wrapper);
    }

    @Override
    public String addCategory(Category category, Long shopId) {
        category.setShopId(shopId);
        save(category);
        return "添加成功";
    }

    @Override
    public String updateCategory(Category category, Long shopId) {
        Category existing = getById(category.getId());
        if (existing == null) {
            throw new RuntimeException("分类不存在");
        }
        if (!existing.getShopId().equals(shopId)) {
            throw new RuntimeException("无权操作他人店铺分类");
        }
        category.setShopId(shopId);
        updateById(category);
        return "修改成功";
    }

    @Override
    public String deleteCategory(Long categoryId, Long shopId) {
        Category existing = getById(categoryId);
        if (existing == null) {
            throw new RuntimeException("分类不存在");
        }
        if (!existing.getShopId().equals(shopId)) {
            throw new RuntimeException("无权操作他人店铺分类");
        }
        removeById(categoryId);
        return "删除成功";
    }

    @Override
    public List<Category> getOpenShopCategoryList(Long shopId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getShopId, shopId);
        return list(wrapper);
    }
}