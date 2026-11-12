package org.example.campus_order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单详情视图对象
 * 用于返回订单完整信息给前端，包含订单项列表
 */
@Data
public class OrderDetailVO {

    /**
     * 订单ID
     */
    private String id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名（关联查询）
     */
    private String username;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 店铺名称（关联查询）
     */
    private String shopName;

    /**
     * 总价
     */
    private BigDecimal totalPrice;

    /**
     * 状态：0=待支付, 1=待接单, 2=已完成, 3=已取消
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 完成时间
     */
    private Date finishTime;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否已评价
     */
    private Boolean hasComment;

    /**
     * 订单项列表
     */
    private List<OrderItemVO> orderItems;

    /**
     * 订单项视图对象（内部类）
     */
    @Data
    public static class OrderItemVO {
        /**
         * 订单项ID
         */
        private Long id;

        /**
         * 菜品ID
         */
        private Long foodId;

        /**
         * 菜品名称
         */
        private String foodName;

        /**
         * 单价
         */
        private BigDecimal price;

        /**
         * 数量
         */
        private Integer num;

        /**
         * 小计
         */
        private BigDecimal subtotal;
    }
}
