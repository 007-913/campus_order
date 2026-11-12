package org.example.campus_order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.campus_order.entity.Food;


@Mapper
public interface FoodMapper extends BaseMapper<Food> {
}
