package org.example.campus_order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("notice")
public class Notice {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String content;

    /**
     * 公告类型：0 平台公告，1 店铺公告
     */
    private Integer type;

    /**
     * 店铺公告所属店铺ID
     */
    private Long shopId;

    private Date createTime;

    @TableLogic
    private Integer deleted;

}
