package org.example.campus_order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.campus_order.entity.Notice;
import java.util.List;

public interface NoticeService extends IService<Notice> {

    /**
     * 查询公开公告列表（未删除、按创建时间倒序）
     */
    List<Notice> getNoticeList();

    /**
     * 查询指定店铺公告列表
     */
    List<Notice> getShopNoticeList(Long shopId);

    /**
     * 获取公告详情，校验未删除
     */
    Notice getNoticeDetail(Long id);

    /**
     * 管理员新增公告，自动填充创建时间、deleted=0
     */
    String addNotice(Notice notice);

    /**
     * 商家新增自己店铺公告
     */
    String addShopNotice(Notice notice, Long shopId);

    /**
     * 管理员修改公告，过滤已删除公告、更新时间
     */
    String updateNotice(Notice notice);

    /**
     * 商家修改自己店铺公告
     */
    String updateShopNotice(Notice notice, Long shopId);

    /**
     * 管理员逻辑删除公告，直接更新deleted=1
     */
    String deleteNotice(Long id);

    /**
     * 商家删除自己店铺公告
     */
    String deleteShopNotice(Long id, Long shopId);
}
