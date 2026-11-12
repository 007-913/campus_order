package org.example.campus_order.vo;

import lombok.Data;

import java.util.Date;

/**
 * 评论视图对象
 * 用于返回评论信息给前端，包含关联的用户名和店铺名
 */
@Data
public class CommentVO {

    /**
     * 评论ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名（关联查询）
     */
    private String username;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 店铺名称（关联查询）
     */
    private String shopName;

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

    /**
     * 创建时间
     */
    private Date createTime;
}