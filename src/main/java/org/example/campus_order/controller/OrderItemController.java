package org.example.campus_order.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.campus_order.entity.OrderItem;
import org.example.campus_order.service.OrderItemService;
import org.example.campus_order.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单明细控制器
 */
@RestController
@RequestMapping("/order/item")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    /**
     * 查看订单明细（需登录，校验权限）
     */
    @GetMapping("/list")
    public Result<List<OrderItem>> list(@RequestParam String orderId, HttpServletRequest request) {
        Long loginId = (Long) request.getAttribute("loginId");
        Integer loginRole = (Integer) request.getAttribute("loginRole");
        Long loginShopId = (Long) request.getAttribute("loginShopId");

        List<OrderItem> itemList = orderItemService.getOrderItemByOrderId(orderId, loginId, loginRole, loginShopId);
        return Result.success(itemList);
    }
}