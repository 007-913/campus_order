package org.example.campus_order.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.campus_order.entity.Notice;
import org.example.campus_order.mapper.NoticeMapper;
import org.example.campus_order.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>
        implements NoticeService {

    @Override
    public List<Notice> getNoticeList() {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getDeleted, 0);
        wrapper.and(w -> w.eq(Notice::getType, 0).or().isNull(Notice::getType));
        wrapper.orderByDesc(Notice::getCreateTime);
        return list(wrapper);
    }

    @Override
    public List<Notice> getShopNoticeList(Long shopId) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getDeleted, 0);
        wrapper.eq(Notice::getShopId, shopId);
        wrapper.and(w -> w.eq(Notice::getType, 1).or().isNull(Notice::getType));
        wrapper.orderByDesc(Notice::getCreateTime);
        return list(wrapper);
    }

    @Override
    public Notice getNoticeDetail(Long id) {
        Notice notice = getById(id);
        if (notice == null || notice.getDeleted() == 1) {
            throw new RuntimeException("公告不存在");
        }
        return notice;
    }

    @Override
    public String addNotice(Notice notice) {
        checkNoticeContent(notice);
        notice.setType(0);
        notice.setShopId(null);
        notice.setCreateTime(new Date());
        notice.setDeleted(0);
        save(notice);
        return "发布成功";
    }

    @Override
    public String addShopNotice(Notice notice, Long shopId) {
        checkShopId(shopId);
        checkNoticeContent(notice);
        notice.setId(null);
        notice.setType(1);
        notice.setShopId(shopId);
        notice.setCreateTime(new Date());
        notice.setDeleted(0);
        save(notice);
        return "发布成功";
    }

    @Override
    public String updateNotice(Notice notice) {
        Notice old = getById(notice.getId());
        if (old == null || old.getDeleted() == 1) {
            throw new RuntimeException("公告不存在");
        }
        if (old.getType() != null && old.getType() != 0) {
            throw new RuntimeException("只能修改平台公告");
        }
        if (notice.getTitle() != null) old.setTitle(notice.getTitle());
        if (notice.getContent() != null) old.setContent(notice.getContent());
        checkNoticeContent(old);
        old.setCreateTime(new Date());
        updateById(old);
        return "修改成功";
    }

    @Override
    public String updateShopNotice(Notice notice, Long shopId) {
        checkShopId(shopId);
        Notice old = getById(notice.getId());
        if (old == null || old.getDeleted() == 1 || old.getType() == null || old.getType() != 1
                || !shopId.equals(old.getShopId())) {
            throw new RuntimeException("店铺公告不存在");
        }
        if (notice.getTitle() != null) old.setTitle(notice.getTitle());
        if (notice.getContent() != null) old.setContent(notice.getContent());
        checkNoticeContent(old);
        old.setCreateTime(new Date());
        updateById(old);
        return "修改成功";
    }

    @Override
    public String deleteNotice(Long id) {
        LambdaUpdateWrapper<Notice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notice::getId, id)
                .and(w -> w.eq(Notice::getType, 0).or().isNull(Notice::getType))
                .set(Notice::getDeleted, 1);
        int updateRows = baseMapper.update(null, updateWrapper);
        if (updateRows <= 0) {
            throw new RuntimeException("公告不存在，删除失败");
        }
        return "删除成功";
    }

    @Override
    public String deleteShopNotice(Long id, Long shopId) {
        checkShopId(shopId);
        LambdaUpdateWrapper<Notice> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Notice::getId, id)
                .eq(Notice::getShopId, shopId)
                .eq(Notice::getType, 1)
                .set(Notice::getDeleted, 1);
        int updateRows = baseMapper.update(null, updateWrapper);
        if (updateRows <= 0) {
            throw new RuntimeException("店铺公告不存在，删除失败");
        }
        return "删除成功";
    }

    private void checkShopId(Long shopId) {
        if (shopId == null) {
            throw new RuntimeException("店铺信息不存在");
        }
    }

    private void checkNoticeContent(Notice notice) {
        if (notice.getTitle() == null || notice.getTitle().trim().isEmpty()) {
            throw new RuntimeException("公告标题不能为空");
        }
        if (notice.getContent() == null || notice.getContent().trim().isEmpty()) {
            throw new RuntimeException("公告内容不能为空");
        }
    }
}
