package org.example.campus_order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.campus_order.entity.Order;
import org.example.campus_order.vo.OrderVO;

import java.util.List;


public interface OrderService extends IService<Order> {

    /**
     * 学生下单
     */
    String createOrder(Order order, Long userId);

    /**
     * 学生查看自己的订单列表（带是否已评价）
     */
    List<OrderVO> getUserOrderListWithComment(Long userId);

    /**
     * 商家查看自己店铺订单
     */
    List<Order> getShopOrderList(Long shopId);

    /**
     * 商家接单
     */
    String acceptOrder(String id, Long shopId);

    /**
     * 商家完成订单
     */
    String finishOrder(String id, Long shopId);

    /**
     * 学生取消订单
     */
    String cancelOrder(String id, Long userId);
}
