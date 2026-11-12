package org.example.campus_order.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.campus_order.entity.Order;
import org.example.campus_order.entity.Shop;
import org.example.campus_order.mapper.OrderMapper;
import org.example.campus_order.mapper.ShopMapper;
import org.example.campus_order.service.ShopService;
import org.example.campus_order.utils.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public Shop getMineShopInfo(Long shopId) {
        Shop shop = getById(shopId);
        if (shop == null) {
            throw new BusinessException("店铺不存在");
        }
        // 脱敏，不返回密码
        shop.setPassword(null);
        shop.setRole(null);
        return shop;
    }

    @Override
    public String updateShopBusinessStatus(Long shopId, Integer status) {
        if (status != 0 && status != 1) {
            throw new BusinessException("状态错误，0=打烊 1=营业");
        }
        Shop shop = getById(shopId);
        if (shop == null) {
            throw new BusinessException("店铺不存在");
        }
        // 管理员已封禁，商家不能再切换营业状态
        if (shop.getStatus() != null && shop.getStatus() == 2) {
            throw new BusinessException("账号已被禁用，无法操作店铺营业状态");
        }

        // 这里改成 businessStatus，不是 status
        shop.setBusinessStatus(status);
        updateById(shop);
        return status == 1 ? "开始营业" : "暂停营业";
    }

    @Override
    public String updateShopBaseInfo(Shop paramShop, Long shopId) {
        Shop old = getById(shopId);
        if (old == null) {
            throw new BusinessException("店铺不存在");
        }

        // 管理员已封禁，不允许修改
        if (old.getStatus() != null && old.getStatus() == 2) {
            throw new BusinessException("账号已被禁用，无法修改店铺信息");
        }

        // 只允许修改部分字段，禁止篡改 id/username/password/status/businessStatus/role
        if (paramShop.getShopName() != null) {
            old.setShopName(paramShop.getShopName());
        }
        if (paramShop.getPhone() != null) {
            old.setPhone(paramShop.getPhone());
        }
        if (paramShop.getAddress() != null) {
            old.setAddress(paramShop.getAddress());
        }
        if (paramShop.getCover() != null) {
            old.setCover(paramShop.getCover());
        }

        updateById(old);
        return "修改成功";
    }

    @Override
    public List<Shop> getOpenShopList() {
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        // 只显示管理员未封禁的店铺
        wrapper.eq(Shop::getStatus, 1);

        List<Shop> shops = list(wrapper);

        // 脱敏：去掉密码和角色
        shops.forEach(s -> {
            s.setPassword(null);
            s.setRole(null);
        });
        fillCompletedOrderStats(shops);
        return shops;
    }

    @Override
    public Shop getOpenShopDetail(Long shopId) {
        Shop shop = getById(shopId);
        if (shop == null) {
            throw new BusinessException("店铺不存在");
        }

        // 被管理员封禁的店铺，用户端不可查看
        if (shop.getStatus() != null && shop.getStatus() == 2) {
            throw new BusinessException("店铺不存在或已关闭");
        }

        shop.setPassword(null);
        shop.setRole(null);
        List<Shop> shops = new ArrayList<>();
        shops.add(shop);
        fillCompletedOrderStats(shops);
        return shop;
    }

    private void fillCompletedOrderStats(List<Shop> shops) {
        if (shops == null || shops.isEmpty()) {
            return;
        }

        Map<Long, Shop> shopMap = new HashMap<>();
        shops.forEach(shop -> {
            shop.setSales(0);
            shop.setCompletedAmount(BigDecimal.ZERO);
            shop.setAvgPrice(BigDecimal.ZERO);
            shopMap.put(shop.getId(), shop);
        });

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getStatus, 2);
        List<Order> completedOrders = orderMapper.selectList(wrapper);

        Map<Long, Integer> countMap = new HashMap<>();
        Map<Long, BigDecimal> amountMap = new HashMap<>();
        completedOrders.forEach(order -> {
            if (order.getShopId() == null || !shopMap.containsKey(order.getShopId())) {
                return;
            }
            BigDecimal price = order.getTotalPrice() == null ? BigDecimal.ZERO : order.getTotalPrice();
            countMap.put(order.getShopId(), countMap.getOrDefault(order.getShopId(), 0) + 1);
            amountMap.put(order.getShopId(), amountMap.getOrDefault(order.getShopId(), BigDecimal.ZERO).add(price));
        });

        shopMap.forEach((shopId, shop) -> {
            int count = countMap.getOrDefault(shopId, 0);
            BigDecimal amount = amountMap.getOrDefault(shopId, BigDecimal.ZERO);
            shop.setSales(count);
            shop.setCompletedAmount(amount);
            if (count > 0) {
                shop.setAvgPrice(amount.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP));
            }
        });
    }
}
