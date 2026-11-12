package org.example.campus_order.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.campus_order.dto.CommentDTO;
import org.example.campus_order.entity.Comment;
import org.example.campus_order.service.CommentService;
import org.example.campus_order.utils.Result;
import org.example.campus_order.utils.RoleCheckUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 评价控制器
 * 学生评价: /comment/add
 * 商家查看自己店铺评价: /comment/list
 * 学生查看店铺评价: /comment/open/list
 * 管理员删除评价: /comment/delete/{id}
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 学生评价
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody CommentDTO commentDTO, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isUser(role)) {
            return Result.error("无权限访问");
        }
        Long userId = (Long) request.getAttribute("loginId");
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        String msg = commentService.addComment(comment, userId);
        return Result.success(msg);
    }

    /**
     * 商家查看自己店铺的评价
     */
    @GetMapping("/list")
    public Result<?> list(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        return Result.success(commentService.getShopCommentList(shopId));
    }

    /**
     * 学生查看某店铺评价（公开）
     */
    @GetMapping("/open/list")
    public Result<?> openList(@RequestParam Long shopId) {
        return Result.success(commentService.getOpenCommentList(shopId));
    }

    /**
     * 商家/管理员删除评价
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (RoleCheckUtil.isAdmin(role)) {
            String msg = commentService.deleteCommentByAdmin(id);
            return Result.success(msg);
        }
        if (RoleCheckUtil.isShop(role)) {
            Long shopId = getLoginShopId(request);
            String msg = commentService.deleteCommentByShop(id, shopId);
            return Result.success(msg);
        }
        return Result.error("无权限访问");
    }

    // 统一提取登录商家shopId工具方法
    private Long getLoginShopId(HttpServletRequest request) {
        Long shopId = (Long) request.getAttribute("loginShopId");
        if (shopId == null) {
            shopId = (Long) request.getAttribute("loginId");
        }
        return shopId;
    }
}