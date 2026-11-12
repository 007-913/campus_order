package org.example.campus_order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("food")
public class Food {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long shopId;
    private Long categoryId;

    private String foodName;

    private Double price;

    private String image;

    private Integer status;

    private String description;
}
