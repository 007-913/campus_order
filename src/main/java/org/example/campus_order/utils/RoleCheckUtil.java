package org.example.campus_order.utils;

/**
 * 角色检查工具
 * 角色统一: 0=管理员, 1=商家, 2=学生
 */
public class RoleCheckUtil {

    public static boolean isAdmin(Integer role) {
        return role != null && role == JwtUtil.ROLE_ADMIN;
    }

    public static boolean isShop(Integer role) {
        return role != null && role == JwtUtil.ROLE_SHOP;
    }

    public static boolean isUser(Integer role) {
        return role != null && role == JwtUtil.ROLE_USER;
    }
}
