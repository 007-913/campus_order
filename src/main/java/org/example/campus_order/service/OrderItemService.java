package org.example.campus_order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.campus_order.entity.OrderItem;
import java.util.List;

public interface OrderItemService extends IService<OrderItem> {

    /**
     * 根据订单号查询明细，同时校验用户/商家/管理员查看权限
     * @param orderId 订单编号
     * @param loginId 当前登录用户ID
     * @param loginRole 当前登录角色
     * @param loginShopId 当前登录商家店铺ID
     * @return 订单明细列表
     */
    List<OrderItem> getOrderItemByOrderId(String orderId, Long loginId, Integer loginRole, Long loginShopId);
}