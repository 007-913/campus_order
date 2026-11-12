package org.example.campus_order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.campus_order.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {

    /**
     * 学生提交评价
     */
    String addComment(Comment comment, Long userId);

    /**
     * 商家查看自己店铺评价
     */
    List<Comment> getShopCommentList(Long shopId);

    /**
     * 用户公开查看店铺评价
     */
    List<Comment> getOpenCommentList(Long shopId);

    /**
     * 管理员删除评价
     */
    String deleteCommentByAdmin(Long id);

    /**
     * 商家删除自己店铺评价
     */
    String deleteCommentByShop(Long id, Long shopId);
}