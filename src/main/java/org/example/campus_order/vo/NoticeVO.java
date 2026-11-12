package org.example.campus_order.vo;

import lombok.Data;

import java.util.Date;

/**
 * 公告视图对象
 * 用于返回公告信息给前端
 */
@Data
public class NoticeVO {

    /**
     * 公告ID
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 公告类型：0 平台公告，1 店铺公告
     */
    private Integer type;

    /**
     * 店铺公告所属店铺ID
     */
    private Long shopId;

    /**
     * 创建时间
     */
    private Date createTime;
}
