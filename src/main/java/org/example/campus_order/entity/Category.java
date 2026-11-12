package org.example.campus_order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.util.Date;

@Data
@TableName("category")
public class Category {

    @TableId(type = IdType.AUTO)
    private  Long id;

    private  Long shopId;

    private  String name;

    private Date createTime;
}
