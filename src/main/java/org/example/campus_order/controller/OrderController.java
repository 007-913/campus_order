package org.example.campus_order.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.campus_order.dto.OrderDTO;
import org.example.campus_order.entity.Order;
import org.example.campus_order.service.OrderService;
import org.example.campus_order.utils.Result;
import org.example.campus_order.utils.RoleCheckUtil;
import org.example.campus_order.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 * 订单状态: 0=待支付, 1=待接单, 2=已完成, 3=已取消
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 学生下单
     */
    @PostMapping("/create")
    public Result<String> create(@RequestBody OrderDTO orderDTO, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isUser(role)) {
            return Result.error("无权限访问");
        }
        Long userId = (Long) request.getAttribute("loginId");
        Order order = new Order();
        BeanUtils.copyProperties(orderDTO, order);
        String msg = orderService.createOrder(order, userId);
        return Result.success(msg);
    }

    /**
     * 学生查看自己的订单列表
     */
    @GetMapping("/list")
    public Result<List<OrderVO>> list(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isUser(role)) {
            return Result.error("无权限访问");
        }
        Long userId = (Long) request.getAttribute("loginId");
        return Result.success(orderService.getUserOrderListWithComment(userId));
    }

    /**
     * 商家查看自己店铺的订单
     */
    @GetMapping("/shop/list")
    public Result<?> shopList(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        return Result.success(orderService.getShopOrderList(shopId));
    }

    /**
     * 商家接单
     */
    @PutMapping("/accept/{id}")
    public Result<String> accept(@PathVariable String id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        String msg = orderService.acceptOrder(id, shopId);
        return Result.success(msg);
    }

    /**
     * 商家完成订单
     */
    @PutMapping("/finish/{id}")
    public Result<String> finish(@PathVariable String id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        String msg = orderService.finishOrder(id, shopId);
        return Result.success(msg);
    }

    /**
     * 学生取消订单
     */
    @PutMapping("/cancel/{id}")
    public Result<String> cancel(@PathVariable String id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isUser(role)) {
            return Result.error("无权限访问");
        }
        Long userId = (Long) request.getAttribute("loginId");
        String msg = orderService.cancelOrder(id, userId);
        return Result.success(msg);
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
