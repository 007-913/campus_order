package org.example.campus_order.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("orders")
public class Order {

    @TableId
    private String id;

    private Long userId;

    private Long shopId;

    private BigDecimal totalPrice;

    private Integer status;

    private Date createTime;

    private Date finishTime;

    private String address;

    private String phone;

    private String remark;
}
