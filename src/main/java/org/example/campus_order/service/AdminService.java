package org.example.campus_order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.campus_order.entity.Admin;
import org.example.campus_order.entity.Shop;
import org.example.campus_order.entity.User;
import java.util.List;

public interface AdminService extends IService<Admin> {
    List<User> getUserList();
    String toggleUser(Long id);
    List<Shop> getShopList();
    String deleteShop(Long id);
    String addShop(Shop shop);
    String toggleShop(Long id);
}