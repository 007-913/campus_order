package org.example.campus_order.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.campus_order.dto.FoodDTO;
import org.example.campus_order.entity.Food;
import org.example.campus_order.entity.FoodQuery;
import org.example.campus_order.service.FoodService;
import org.example.campus_order.utils.Result;
import org.example.campus_order.utils.RoleCheckUtil;
import org.example.campus_order.vo.FoodVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 菜品控制器
 * 商家管理自己菜品: /shop/food/**
 * 学生公开查看: /food/open/**
 */
@RestController
public class FoodController {

    @Autowired
    private FoodService foodService;

    // ==================== 商家端 ====================
    @GetMapping("/shop/food/list")
    public Result<?> list(HttpServletRequest request, FoodQuery query) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        return Result.success(foodService.getShopFoodList(shopId, query));
    }

    @PostMapping("/shop/food/add")
    public Result<String> add(@RequestBody FoodDTO foodDTO, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        Food food = new Food();
        BeanUtils.copyProperties(foodDTO, food);
        String msg = foodService.addFood(food, shopId);
        return Result.success(msg);
    }

    @PutMapping("/shop/food/update")
    public Result<String> update(@RequestBody FoodDTO foodDTO, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        Food food = new Food();
        BeanUtils.copyProperties(foodDTO, food);
        String msg = foodService.updateFood(food, shopId);
        return Result.success(msg);
    }

    @DeleteMapping("/shop/food/delete/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        String msg = foodService.deleteFood(id, shopId);
        return Result.success(msg);
    }

    @PutMapping("/shop/food/toggle/{id}")
    public Result<String> toggle(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        String msg = foodService.toggleFoodStatus(id, shopId);
        return Result.success(msg);
    }

    // ==================== 学生端（公开） ====================
    @GetMapping("/food/open/list")
    public Result<?> openList(@RequestParam Long shopId) {
        return Result.success(foodService.getOpenShopFood(shopId));
    }

    @GetMapping("/food/open/detail/{id}")
    public Result<?> openDetail(@PathVariable Long id) {
        Food food = foodService.getById(id);
        if (food == null) {
            return Result.error("菜品不存在");
        }
        FoodVO foodVO = new FoodVO();
        BeanUtils.copyProperties(food, foodVO);
        return Result.success(foodVO);
    }

    /**
     * 统一提取登录商家shopId工具方法
     */
    private Long getLoginShopId(HttpServletRequest request) {
        Long shopId = (Long) request.getAttribute("loginShopId");
        if (shopId == null) {
            shopId = (Long) request.getAttribute("loginId");
        }
        return shopId;
    }
}