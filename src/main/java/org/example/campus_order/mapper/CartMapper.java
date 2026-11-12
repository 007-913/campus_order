package org.example.campus_order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.campus_order.entity.Cart;

@Mapper
public interface CartMapper  extends BaseMapper<Cart> {
}
