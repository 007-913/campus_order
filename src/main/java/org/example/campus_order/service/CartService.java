package org.example.campus_order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.campus_order.entity.Cart;
import org.example.campus_order.vo.CartVO;
import java.util.List;

public interface CartService extends IService<Cart> {

    /**
     * 查询用户全部购物车
     */
    List<CartVO> getUserCartList(Long userId);

    /**
     * 添加购物车（包含菜品/店铺校验、同店限制、数量合并逻辑）
     */
    String addCart(Cart cart, Long userId);

    /**
     * 修改购物车商品数量
     */
    String updateCartNum(Cart cart, Long userId);

    /**
     * 删除单条购物车项
     */
    String deleteCartItem(Long cartId, Long userId);

    /**
     * 清空当前用户购物车
     */
    String clearUserCart(Long userId);
}