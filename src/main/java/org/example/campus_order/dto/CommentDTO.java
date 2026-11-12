package org.example.campus_order.dto;

import lombok.Data;

/**
 * 评论数据传输对象
 * 用于学生添加评论
 */
@Data
public class CommentDTO {

    /**
     * 评论ID（更新时需要）
     */
    private Long id;

    /**
     * 用户ID（后端自动填充）
     */
    private Long userId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评分（1-5分）
     */
    private Integer score;
}