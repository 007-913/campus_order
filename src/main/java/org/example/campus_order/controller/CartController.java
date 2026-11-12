package org.example.campus_order.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.campus_order.entity.Cart;
import org.example.campus_order.service.CartService;
import org.example.campus_order.utils.Result;
import org.example.campus_order.utils.RoleCheckUtil;
import org.example.campus_order.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器 - 学生操作
 */
@RestController
@RequestMapping("/user/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 查询当前用户购物车
     */
    @GetMapping("/list")
    public Result<List<CartVO>> list(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isUser(role)) {
            return Result.error("无权限访问");
        }
        Long userId = (Long) request.getAttribute("loginId");
        List<CartVO> cartVOList = cartService.getUserCartList(userId);
        return Result.success(cartVOList);
    }

    /**
     * 加入购物车
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody Cart cart, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isUser(role)) {
            return Result.error("无权限访问");
        }
        Long userId = (Long) request.getAttribute("loginId");
        String msg = cartService.addCart(cart, userId);
        return Result.success(msg);
    }

    /**
     * 修改商品数量
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody Cart cart, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isUser(role)) {
            return Result.error("无权限访问");
        }
        Long userId = (Long) request.getAttribute("loginId");
        String msg = cartService.updateCartNum(cart, userId);
        return Result.success(msg);
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isUser(role)) {
            return Result.error("无权限访问");
        }
        Long userId = (Long) request.getAttribute("loginId");
        String msg = cartService.deleteCartItem(id, userId);
        return Result.success(msg);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public Result<String> clear(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isUser(role)) {
            return Result.error("无权限访问");
        }
        Long userId = (Long) request.getAttribute("loginId");
        String msg = cartService.clearUserCart(userId);
        return Result.success(msg);
    }
}