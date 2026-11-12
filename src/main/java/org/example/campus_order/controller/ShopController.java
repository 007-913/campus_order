package org.example.campus_order.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.campus_order.dto.ShopDTO;
import org.example.campus_order.entity.Shop;
import org.example.campus_order.service.ShopService;
import org.example.campus_order.utils.Result;
import org.example.campus_order.utils.RoleCheckUtil;
import org.example.campus_order.vo.ShopVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 店铺控制器
 * 商家管理自己店铺: /shop/**
 * 学生公开查看: /shop/open/**
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 商家查看自己店铺详情
     */
    @GetMapping("/me")
    public Result<ShopVO> me(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        Shop shop = shopService.getMineShopInfo(shopId);
        ShopVO shopVO = new ShopVO();
        BeanUtils.copyProperties(shop, shopVO);
        return Result.success(shopVO);
    }

    /**
     * 商家修改营业状态 0=停业 1=营业
     */
    @PutMapping("/status")
    public Result<String> updateStatus(@RequestParam Integer status, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        String msg = shopService.updateShopBusinessStatus(shopId, status);
        return Result.success(msg);
    }

    /**
     * 商家修改店铺信息
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody ShopDTO shopDTO, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        Shop shop = new Shop();
        BeanUtils.copyProperties(shopDTO, shop);
        String msg = shopService.updateShopBaseInfo(shop, shopId);
        return Result.success(msg);
    }

    // ==================== 公开接口（学生端） ====================
    /**
     * 学生查看营业中的店铺列表
     */
    @GetMapping("/open/list")
    public Result<List<ShopVO>> openList() {
        List<Shop> shopList = shopService.getOpenShopList();
        List<ShopVO> shopVOList = shopList.stream().map(shop -> {
            ShopVO shopVO = new ShopVO();
            BeanUtils.copyProperties(shop, shopVO);
            return shopVO;
        }).collect(Collectors.toList());
        return Result.success(shopVOList);
    }

    /**
     * 学生查看店铺详情
     */
    @GetMapping("/open/{id}")
    public Result<ShopVO> openDetail(@PathVariable Long id) {
        Shop shop = shopService.getOpenShopDetail(id);
        ShopVO shopVO = new ShopVO();
        BeanUtils.copyProperties(shop, shopVO);
        return Result.success(shopVO);
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