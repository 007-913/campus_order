package org.example.campus_order.dto;

import lombok.Data;

/**
 * 公告数据传输对象
 * 用于管理员添加和更新公告
 */
@Data
public class NoticeDTO {

    /**
     * 公告ID（更新时需要）
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
     * 店铺ID，店铺公告使用
     */
    private Long shopId;
}
