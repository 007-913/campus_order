package org.example.campus_order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.campus_order.entity.Order;
import org.example.campus_order.vo.DailyStatVO;

import java.util.Date;
import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 按天聚合已完成订单的订单数与营业额（数据库一次分组，避免逐天全表查询）
     */
    @Select("""
            <script>
            SELECT DATE(COALESCE(finish_time, create_time)) AS day,
                   COUNT(*) AS cnt,
                   COALESCE(SUM(total_price), 0) AS money
            FROM orders
            WHERE status = 2
              AND COALESCE(finish_time, create_time) &gt;= #{start}
              AND COALESCE(finish_time, create_time) &lt; #{end}
            <if test="shopId != null">
              AND shop_id = #{shopId}
            </if>
            GROUP BY DATE(COALESCE(finish_time, create_time))
            ORDER BY day
            </script>
            """)
    List<DailyStatVO> selectDailyStat(@Param("shopId") Long shopId,
                                      @Param("start") Date start,
                                      @Param("end") Date end);
}
