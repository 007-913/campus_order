package org.example.campus_order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("shop")
public class Shop {
    @TableId(type = IdType.AUTO)
    private Long id;
    //店铺名字
    private String shopName;
    //商家名字
    private String username;
    //密码
    private String password;

    private String phone;
    private String address;
    private String cover;
    /**
     * 管理员控制
     * 1 正常
     * 2 封禁
     */
    private Integer status;

    /**
     * 商家控制
     * 1 营业
     * 0 打烊
     */
    private Integer businessStatus;


    @TableField(exist = false)
    private Integer role;

    @TableField(exist = false)
    private Integer sales;

    @TableField(exist = false)
    private BigDecimal avgPrice;

    @TableField(exist = false)
    private BigDecimal completedAmount;

    private Date createTime;
}
