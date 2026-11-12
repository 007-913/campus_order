package org.example.campus_order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.campus_order.entity.Shop;
import java.util.List;

public interface ShopService extends IService<Shop> {

    /**
     * 商家查询自身店铺信息，脱敏隐藏密码
     */
    Shop getMineShopInfo(Long shopId);

    /**
     * 修改店铺营业状态（0停业 / 1营业）
     */
    String updateShopBusinessStatus(Long shopId, Integer status);

    /**
     * 商家修改店铺基础信息，禁止篡改敏感字段
     */
    String updateShopBaseInfo(Shop paramShop, Long shopId);

    /**
     * 查询所有营业中店铺，脱敏密码、角色字段
     */
    List<Shop> getOpenShopList();

    /**
     * 公开查询单家店铺详情，脱敏密码、角色字段
     */
    Shop getOpenShopDetail(Long shopId);
}