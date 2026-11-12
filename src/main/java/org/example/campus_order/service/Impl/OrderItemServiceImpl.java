package org.example.campus_order.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.campus_order.entity.Order;
import org.example.campus_order.entity.OrderItem;
import org.example.campus_order.mapper.OrderItemMapper;
import org.example.campus_order.mapper.OrderMapper;
import org.example.campus_order.service.OrderItemService;
import org.example.campus_order.utils.BusinessException;
import org.example.campus_order.utils.RoleCheckUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemServiceImpl
        extends ServiceImpl<OrderItemMapper, OrderItem>
        implements OrderItemService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public List<OrderItem> getOrderItemByOrderId(String orderId, Long loginId, Integer loginRole, Long loginShopId) {
        if (loginId == null) {
            throw new BusinessException("未登录");
        }

        // 校验订单归属
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (RoleCheckUtil.isUser(loginRole)) {
            if (!order.getUserId().equals(loginId)) {
                throw new BusinessException("无权查看他人订单");
            }
        } else if (RoleCheckUtil.isShop(loginRole)) {
            if (loginShopId == null) {
                throw new BusinessException("商家信息异常");
            }
            if (!order.getShopId().equals(loginShopId)) {
                throw new BusinessException("无权查看他人店铺订单");
            }
        } else if (!RoleCheckUtil.isAdmin(loginRole)) {
            throw new BusinessException("无权限访问");
        }

        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        return list(wrapper);
    }
}