package org.example.campus_order.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.campus_order.entity.Admin;
import org.example.campus_order.entity.Order;
import org.example.campus_order.entity.Shop;
import org.example.campus_order.entity.User;
import org.example.campus_order.mapper.AdminMapper;
import org.example.campus_order.service.AdminService;
import org.example.campus_order.service.OrderService;
import org.example.campus_order.service.ShopService;
import org.example.campus_order.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private UserService userService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private OrderService orderService;

    @Override
    public List<User> getUserList() {
        List<User> list = userService.list();
        // 脱敏：不返回密码
        list.forEach(u -> u.setPassword(null));
        return list;
    }

    @Override
    public String toggleUser(Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        userService.updateById(user);
        return user.getStatus() == 0 ? "已禁用" : "已解禁";
    }

    @Override
    public List<Shop> getShopList() {
        List<Shop> list = shopService.list();
        // 脱敏：不返回密码
        list.forEach(s -> s.setPassword(null));
        return list;
    }

    @Override
    public String deleteShop(Long id) {
        long orderCount = orderService.lambdaQuery()
                .eq(Order::getShopId, id)
                .in(Order::getStatus, 0, 1)
                .count();
        if (orderCount > 0) {
            throw new RuntimeException("该商家有未完成订单，无法删除");
        }
        shopService.removeById(id);
        return "删除成功";
    }

    @Override
    public String addShop(Shop shop) {
        long count = shopService.lambdaQuery()
                .eq(Shop::getUsername, shop.getUsername())
                .count();
        if (count > 0) {
            throw new RuntimeException("商家账号已存在");
        }
        shop.setStatus(1);
        shopService.save(shop);
        return "添加成功";
    }

    @Override
    public String toggleShop(Long id) {
        Shop shop = shopService.getById(id);
        if (shop == null) {
            throw new RuntimeException("商家不存在");
        }
        shop.setStatus(shop.getStatus() == 2 ? 1 : 2);
        shopService.updateById(shop);
        return shop.getStatus() == 2 ? "已封禁" : "已解封";
    }
}