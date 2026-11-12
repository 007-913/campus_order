package org.example.campus_order.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.campus_order.entity.*;
import org.example.campus_order.mapper.OrderMapper;
import org.example.campus_order.service.*;
import org.example.campus_order.utils.BusinessException;
import org.example.campus_order.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    /** 订单状态常量 */
    private static final int STATUS_PENDING_PAYMENT = 0;
    private static final int STATUS_PENDING_ACCEPT = 1;
    private static final int STATUS_FINISHED = 2;
    private static final int STATUS_CANCELLED = 3;

    @Autowired
    private CartService cartService;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private FoodService foodService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private CommentService commentService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createOrder(Order order, Long userId) {
        // 查购物车
        LambdaQueryWrapper<Cart> cartWrapper = new LambdaQueryWrapper<>();
        cartWrapper.eq(Cart::getUserId, userId);
        List<Cart> cartList = cartService.list(cartWrapper);
        if (cartList.isEmpty()) {
            throw new BusinessException("购物车为空");
        }

        // 确定下单店铺：优先用前端传的shopId，没有则取购物车第一个菜品所属店铺
        Long orderShopId = order.getShopId();
        if (orderShopId == null && !cartList.isEmpty()) {
            Food firstFood = foodService.getById(cartList.get(0).getFoodId());
            if (firstFood != null) {
                orderShopId = firstFood.getShopId();
            }
        }

        // 过滤出该店铺的购物车商品
        Long finalShopId = orderShopId;
        List<Cart> shopCartList = new ArrayList<>();
        for (Cart cart : cartList) {
            Food food = foodService.getById(cart.getFoodId());
            if (food != null && food.getShopId().equals(finalShopId)) {
                shopCartList.add(cart);
            }
        }

        if (shopCartList.isEmpty()) {
            throw new BusinessException("该店铺购物车为空");
        }

        // 校验菜品
        BigDecimal total = BigDecimal.ZERO;
        for (Cart cart : shopCartList) {
            Food food = foodService.getById(cart.getFoodId());
            if (food == null) {
                throw new BusinessException("菜品不存在，请刷新购物车");
            }
            if (food.getStatus() == null || food.getStatus() != 1) {
                throw new BusinessException("菜品「" + food.getFoodName() + "」已下架");
            }
        }

        // 校验店铺营业
        Shop shop = shopService.getById(finalShopId);
        if (shop == null || shop.getStatus() != 1) {
            throw new BusinessException("店铺未营业，无法下单");
        }
        // 商家自主停业后不允许下单
        if (shop.getBusinessStatus() == null || shop.getBusinessStatus() != 1) {
            throw new BusinessException("该店铺已停业，暂不接单");
        }

        // 生成订单ID
        String orderId = UUID.randomUUID().toString().replace("-", "");

        // 创建订单项
        for (Cart cart : shopCartList) {
            Food food = foodService.getById(cart.getFoodId());
            BigDecimal price = BigDecimal.valueOf(food.getPrice());
            total = total.add(price.multiply(BigDecimal.valueOf(cart.getNum())));

            OrderItem item = new OrderItem();
            item.setOrderId(orderId);
            item.setFoodId(food.getId());
            item.setFoodName(food.getFoodName());
            item.setPrice(price);
            item.setNum(cart.getNum());
            orderItemService.save(item);
        }

        // 保存订单
        order.setId(orderId);
        order.setUserId(userId);
        order.setShopId(finalShopId);
        order.setTotalPrice(total);
        order.setStatus(STATUS_PENDING_PAYMENT);
        order.setCreateTime(new Date());
        this.save(order);

        // 只清除本单涉及的购物车商品（同一用户 + 指定购物车ID）
        LambdaQueryWrapper<Cart> removeWrapper = new LambdaQueryWrapper<>();
        removeWrapper.eq(Cart::getUserId, userId)
                .in(Cart::getId, shopCartList.stream().map(Cart::getId).collect(Collectors.toList()));
        cartService.remove(removeWrapper);

        return orderId;
    }


    @Override
    public List<OrderVO> getUserOrderListWithComment(Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime);

        List<Order> orderList = this.list(wrapper);

        return orderList.stream().map(order -> {
            OrderVO vo = new OrderVO();
            vo.setId(order.getId());
            vo.setUserId(order.getUserId());
            vo.setShopId(order.getShopId());
            vo.setTotalPrice(order.getTotalPrice());
            vo.setStatus(order.getStatus());
            vo.setCreateTime(order.getCreateTime());
            vo.setFinishTime(order.getFinishTime());
            vo.setAddress(order.getAddress());
            vo.setPhone(order.getPhone());
            vo.setRemark(order.getRemark());

            long count = commentService.lambdaQuery()
                    .eq(Comment::getOrderId, order.getId())
                    .count();
            vo.setHasComment(count > 0);

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Order> getShopOrderList(Long shopId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getShopId, shopId)
                .ne(Order::getStatus, STATUS_PENDING_PAYMENT)
                .orderByDesc(Order::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public String acceptOrder(String id, Long shopId) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getShopId().equals(shopId)) {
            throw new BusinessException("无权操作他人店铺订单");
        }
        if (order.getStatus() != STATUS_PENDING_ACCEPT) {
            throw new BusinessException("订单状态异常，无法接单");
        }

        return "接单成功";
    }

    @Override
    public String finishOrder(String id, Long shopId) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getShopId().equals(shopId)) {
            throw new BusinessException("无权操作他人店铺订单");
        }
        if (order.getStatus() != STATUS_PENDING_ACCEPT) {
            throw new BusinessException("订单状态异常，待接单订单才能完成");
        }

        order.setStatus(STATUS_FINISHED);
        order.setFinishTime(new Date());
        this.updateById(order);
        return "订单完成";
    }

    @Override
    public String cancelOrder(String id, Long userId) {
        Order order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作他人订单");
        }
        if (order.getStatus() != STATUS_PENDING_PAYMENT && order.getStatus() != STATUS_PENDING_ACCEPT) {
            throw new BusinessException("只能取消待支付或待接单的订单");
        }

        order.setStatus(STATUS_CANCELLED);
        this.updateById(order);
        return "订单已取消";
    }
}
