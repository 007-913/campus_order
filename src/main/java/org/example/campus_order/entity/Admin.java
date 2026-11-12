package org.example.campus_order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName("Admin")
public class Admin {

    @TableId(type = IdType.AUTO)
    private Long id;

    //登录账号
    private String username;

    //密码
    private String password;

    //姓名
    private String name;

    //0=管理员
   private Integer role;

}
