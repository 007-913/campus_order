package org.example.campus_order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
public class OrderVO {

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

    /**
     * 是否已评价
     */
    private Boolean hasComment;
}
