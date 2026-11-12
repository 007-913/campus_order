package org.example.campus_order.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.campus_order.entity.Comment;
import org.example.campus_order.entity.Order;
import org.example.campus_order.mapper.CommentMapper;
import org.example.campus_order.mapper.OrderMapper;
import org.example.campus_order.service.CommentService;
import org.example.campus_order.utils.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl
        extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public String addComment(Comment comment, Long userId) {
        Order order = orderMapper.selectById(comment.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("只能评价自己的订单");
        }

        if (order.getStatus() != 2) {
            throw new BusinessException("只能评价已完成的订单");
        }

        LambdaQueryWrapper<Comment> dupWrapper = new LambdaQueryWrapper<>();
        dupWrapper.eq(Comment::getOrderId, comment.getOrderId());
        if (this.count(dupWrapper) > 0) {
            throw new BusinessException("该订单已评价，不能重复评价");
        }

        comment.setUserId(userId);
        comment.setShopId(order.getShopId());
        comment.setCreateTime(new Date());
        this.save(comment);

        return "评价成功";
    }

    @Override
    public List<Comment> getShopCommentList(Long shopId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getShopId, shopId)
                .orderByDesc(Comment::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<Comment> getOpenCommentList(Long shopId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getShopId, shopId)
                .orderByDesc(Comment::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public String deleteCommentByAdmin(Long id) {
        Comment comment = this.getById(id);
        if (comment == null) {
            throw new BusinessException("评价不存在");
        }
        this.removeById(id);
        return "删除成功";
    }

    @Override
    public String deleteCommentByShop(Long id, Long shopId) {
        Comment comment = this.getById(id);
        if (comment == null) {
            throw new BusinessException("评价不存在");
        }
        if (!comment.getShopId().equals(shopId)) {
            throw new BusinessException("只能删除自己店铺的评价");
        }
        this.removeById(id);
        return "删除成功";
    }
}