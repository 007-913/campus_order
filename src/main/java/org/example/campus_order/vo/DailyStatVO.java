package org.example.campus_order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 按天聚合统计结果
 */
@Data
public class DailyStatVO {

    /** 统计日期 */
    private LocalDate day;

    /** 当日完成订单数 */
    private Long cnt;

    /** 当日营业额 */
    private BigDecimal money;
}
