package org.example.campus_order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.campus_order.entity.Category;
import java.util.List;

public interface CategoryService extends IService<Category> {

    /**
     * 查询当前店铺所有分类
     */
    List<Category> getShopCategoryList(Long shopId);

    /**
     * 新增店铺分类
     */
    String addCategory(Category category, Long shopId);

    /**
     * 修改分类（校验店铺归属）
     */
    String updateCategory(Category category, Long shopId);

    /**
     * 删除分类（校验店铺归属）
     */
    String deleteCategory(Long categoryId, Long shopId);

    /**
     * 用户端查看某店铺分类
     */
    List<Category> getOpenShopCategoryList(Long shopId);
}