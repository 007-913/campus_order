package org.example.campus_order.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.campus_order.entity.Category;
import org.example.campus_order.service.CategoryService;
import org.example.campus_order.utils.Result;
import org.example.campus_order.utils.RoleCheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 分类控制器 - 商家管理自己店铺的分类
 */
@RestController
@RequestMapping("/shop/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 商家查看自己店铺的分类
     */
    @GetMapping("/list")
    public Result<?> list(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        return Result.success(categoryService.getShopCategoryList(shopId));
    }

    /**
     * 商家新增分类
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Category category, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        String msg = categoryService.addCategory(category, shopId);
        return Result.success(msg);
    }

    /**
     * 商家修改分类
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody Category category, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        String msg = categoryService.updateCategory(category, shopId);
        return Result.success(msg);
    }

    /**
     * 商家删除分类
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        String msg = categoryService.deleteCategory(id, shopId);
        return Result.success(msg);
    }

    /**
     * 提取登录商家shopId统一工具方法
     */
    private Long getLoginShopId(HttpServletRequest request) {
        Long shopId = (Long) request.getAttribute("loginShopId");
        if (shopId == null) {
            shopId = (Long) request.getAttribute("loginId");
        }
        return shopId;
    }
}