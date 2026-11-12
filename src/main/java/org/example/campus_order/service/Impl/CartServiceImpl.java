package org.example.campus_order.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.campus_order.entity.Cart;
import org.example.campus_order.entity.Food;
import org.example.campus_order.entity.Shop;
import org.example.campus_order.utils.BusinessException;
import org.example.campus_order.mapper.CartMapper;
import org.example.campus_order.service.CartService;
import org.example.campus_order.service.FoodService;
import org.example.campus_order.service.ShopService;
import org.example.campus_order.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Autowired
    private FoodService foodService;
    @Autowired
    private ShopService shopService;

    @Override
    public List<CartVO> getUserCartList(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        List<Cart> cartList = list(wrapper);

        List<CartVO> cartVOList = new ArrayList<>();
        for (Cart cart : cartList) {
            CartVO cartVO = new CartVO();
            cartVO.setId(cart.getId());
            cartVO.setUserId(cart.getUserId());
            cartVO.setShopId(cart.getShopId());
            cartVO.setFoodId(cart.getFoodId());
            cartVO.setNum(cart.getNum());

            // 关联查询菜品信息
            Food food = foodService.getById(cart.getFoodId());
            if (food != null) {
                cartVO.setFoodName(food.getFoodName());
                cartVO.setFoodImage(food.getImage());
                cartVO.setFoodPrice(food.getPrice());
                cartVO.setSubtotal(food.getPrice() * cart.getNum());
            }

            // 关联查询店铺信息
            Shop shop = shopService.getById(cart.getShopId());
            if (shop != null) {
                cartVO.setShopName(shop.getShopName());
            }

            cartVOList.add(cartVO);
        }
        return cartVOList;
    }

    @Override
    public String addCart(Cart cart, Long userId) {
        // 1. 校验菜品存在
        Food food = foodService.getById(cart.getFoodId());
        if (food == null) {
            throw new BusinessException("菜品不存在");
        }
        // 2. 校验菜品上架
        if (food.getStatus() == null || food.getStatus() != 1) {
            throw new BusinessException("该菜品已下架");
        }
        // 3. 校验店铺营业
        Shop shop = shopService.getById(food.getShopId());
        if (shop == null || shop.getStatus() != 1) {
            throw new BusinessException("店铺未营业");
        }
        // 商家自主停业后不允许加购
        if (shop.getBusinessStatus() == null || shop.getBusinessStatus() != 1) {
            throw new BusinessException("该店铺已停业，暂不接单");
        }


        // 5. 同一用户 + 同一菜品 → 合并数量
        LambdaQueryWrapper<Cart> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getFoodId, cart.getFoodId());
        Cart existing = getOne(existWrapper);

        if (existing != null) {
            existing.setNum(existing.getNum() + (cart.getNum() != null ? cart.getNum() : 1));
            updateById(existing);
        } else {
            cart.setUserId(userId);
            cart.setShopId(food.getShopId());
            if (cart.getNum() == null || cart.getNum() <= 0) {
                cart.setNum(1);
            }
            save(cart);
        }
        return "加入购物车成功";
    }

    @Override
    public String updateCartNum(Cart cart, Long userId) {
        Cart existing = getById(cart.getId());
        if (existing == null) {
            throw new BusinessException("购物车项不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            throw new BusinessException("无权操作他人购物车");
        }
        if (cart.getNum() != null && cart.getNum() <= 0) {
            throw new BusinessException("数量必须大于0");
        }
        if (cart.getNum() != null) {
            existing.setNum(cart.getNum());
        }
        updateById(existing);
        return "修改成功";
    }

    @Override
    public String deleteCartItem(Long cartId, Long userId) {
        Cart existing = getById(cartId);
        if (existing == null) {
            throw new BusinessException("购物车项不存在");
        }
        if (!existing.getUserId().equals(userId)) {
            throw new BusinessException("无权操作他人购物车");
        }
        removeById(cartId);
        return "删除成功";
    }

    @Override
    public String clearUserCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        remove(wrapper);
        return "购物车已清空";
    }
}