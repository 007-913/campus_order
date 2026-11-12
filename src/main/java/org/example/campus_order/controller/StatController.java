package org.example.campus_order.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.campus_order.service.StatService;
import org.example.campus_order.utils.Result;
import org.example.campus_order.utils.RoleCheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 数据统计控制器
 */
@RestController
@RequestMapping("/stat")
public class StatController {

    @Autowired
    private StatService statService;

    /**
     * 管理员统计
     */
    @GetMapping("/admin")
    public Result<Map<String, Object>> adminStat(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isAdmin(role)) {
            return Result.error("无权限访问");
        }
        Map<String, Object> statData = statService.getAdminStatData();
        return Result.success(statData);
    }

    /**
     * 管理员营业额导出数据
     */
    @GetMapping("/admin/revenue")
    public Result<Map<String, Object>> adminRevenue(@RequestParam(defaultValue = "today") String range,
                                                    @RequestParam(required = false) String month,
                                                    HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isAdmin(role)) {
            return Result.error("无权限访问");
        }
        Map<String, Object> statData = month == null || month.isBlank()
                ? statService.getAdminRevenueData(range)
                : statService.getAdminMonthlyRevenueData(month);
        return Result.success(statData);
    }

    /**
     * 管理员近7日完成订单趋势
     */
    @GetMapping("/admin/line")
    public Result<Map<String, Object>> adminLineStat(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isAdmin(role)) {
            return Result.error("无权限访问");
        }
        Map<String, Object> statData = statService.getAdminDailyOrderTrend();
        return Result.success(statData);
    }

    /**
     * 商家统计
     */
    @GetMapping("/shop")
    public Result<Map<String, Object>> shopStat(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        Map<String, Object> statData = statService.getShopStatData(shopId);
        return Result.success(statData);
    }

    /**
     * 商家近15日营业额趋势
     */
    @GetMapping("/shop/line")
    public Result<Map<String, Object>> shopLineStat(@RequestParam(required = false) String month,
                                                    HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        Map<String, Object> statData = statService.getShopDailyRevenue(shopId, month);
        return Result.success(statData);
    }

    /**
     * 商家近15日完成订单趋势
     */
    @GetMapping("/shop/order-line")
    public Result<Map<String, Object>> shopOrderLineStat(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        Map<String, Object> statData = statService.getShopDailyOrderTrend(shopId);
        return Result.success(statData);
    }

    /**
     * 商家营业额导出数据
     */
    @GetMapping("/shop/revenue")
    public Result<Map<String, Object>> shopRevenue(@RequestParam(defaultValue = "today") String range,
                                                   HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        Map<String, Object> statData = statService.getShopRevenueData(shopId, range);
        return Result.success(statData);
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
