package org.example.campus_order.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.campus_order.entity.Shop;
import org.example.campus_order.entity.User;
import org.example.campus_order.service.AdminService;
import org.example.campus_order.utils.Result;
import org.example.campus_order.utils.RoleCheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 管理员首页数据
     */
    @GetMapping("/list")
    public Result<String> list(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isAdmin(role)) {
            return Result.error("无权限访问");
        }
        return Result.success("admin数据");
    }

    /**
     * 用户列表（返回VO）
     */
    @GetMapping("/user/list")
    public Result<List<User>> userList(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isAdmin(role)) {
            return Result.error("无权限访问");
        }
        return Result.success(adminService.getUserList());
    }

    /**
     * 禁用/解禁用户
     */
    @PutMapping("/user/toggle/{id}")
    public Result<String> toggleUser(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isAdmin(role)) {
            return Result.error("无权限访问");
        }
        String msg = adminService.toggleUser(id);
        return Result.success(msg);
    }

    /**
     * 商家列表（返回VO）
     */
    @GetMapping("/shop/list")
    public Result<List<Shop>> shopList(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isAdmin(role)) {
            return Result.error("无权限访问");
        }
        return Result.success(adminService.getShopList());
    }

    /**
     * 删除商家
     */
    @DeleteMapping("/shop/delete/{id}")
    public Result<String> deleteShop(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isAdmin(role)) {
            return Result.error("无权限访问");
        }
        String res = adminService.deleteShop(id);
        return Result.success(res);
    }

    /**
     * 添加商家（入参替换为DTO，不再接收Shop实体）
     */
    @PostMapping("/shop/add")
    public Result<String> addShop(@RequestBody Shop shop, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isAdmin(role)) {
            return Result.error("无权限访问");
        }
        String msg = adminService.addShop(shop);
        return Result.success(msg);
    }

    /**
     * 禁用/解禁商家
     */
    @PutMapping("/shop/toggle/{id}")
    public Result<String> toggleShop(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isAdmin(role)) {
            return Result.error("无权限访问");
        }
        String msg = adminService.toggleShop(id);
        return Result.success(msg);
    }
}