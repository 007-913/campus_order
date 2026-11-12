package org.example.campus_order.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.campus_order.dto.NoticeDTO;
import org.example.campus_order.entity.Notice;
import org.example.campus_order.service.NoticeService;
import org.example.campus_order.utils.Result;
import org.example.campus_order.utils.RoleCheckUtil;
import org.example.campus_order.vo.NoticeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告控制器
 * 公开查看: /notice/list（已排除拦截器）
 * 管理员增删改
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 查看公告列表（公开）
     */
    @GetMapping("/list")
    public Result<List<NoticeVO>> list() {
        List<Notice> noticeList = noticeService.getNoticeList();
        List<NoticeVO> noticeVOList = convertToVOList(noticeList);
        return Result.success(noticeVOList);
    }

    /**
     * 学生端查看指定店铺公告（公开）
     */
    @GetMapping("/shop/list")
    public Result<List<NoticeVO>> shopList(@RequestParam Long shopId) {
        List<Notice> noticeList = noticeService.getShopNoticeList(shopId);
        return Result.success(convertToVOList(noticeList));
    }

    /**
     * 查看公告详情（公开）
     */
    @GetMapping("/detail/{id}")
    public Result<NoticeVO> detail(@PathVariable Long id) {
        Notice notice = noticeService.getNoticeDetail(id);
        NoticeVO noticeVO = new NoticeVO();
        BeanUtils.copyProperties(notice, noticeVO);
        return Result.success(noticeVO);
    }

    /**
     * 管理员发布公告
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody NoticeDTO noticeDTO, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isAdmin(role)) {
            return Result.error("无权限访问");
        }
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO, notice);
        String msg = noticeService.addNotice(notice);
        return Result.success(msg);
    }

    /**
     * 管理员修改公告
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody NoticeDTO noticeDTO, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isAdmin(role)) {
            return Result.error("无权限访问");
        }
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO, notice);
        String msg = noticeService.updateNotice(notice);
        return Result.success(msg);
    }

    /**
     * 管理员逻辑删除公告
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isAdmin(role)) {
            return Result.error("无权限访问");
        }
        String msg = noticeService.deleteNotice(id);
        return Result.success(msg);
    }

    /**
     * 商家查看自己店铺公告
     */
    @GetMapping("/shop/mine")
    public Result<List<NoticeVO>> mine(HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        List<Notice> noticeList = noticeService.getShopNoticeList(shopId);
        return Result.success(convertToVOList(noticeList));
    }

    /**
     * 商家发布自己店铺公告
     */
    @PostMapping("/shop/add")
    public Result<String> shopAdd(@RequestBody NoticeDTO noticeDTO, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO, notice);
        String msg = noticeService.addShopNotice(notice, shopId);
        return Result.success(msg);
    }

    /**
     * 商家修改自己店铺公告
     */
    @PutMapping("/shop/update")
    public Result<String> shopUpdate(@RequestBody NoticeDTO noticeDTO, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        Notice notice = new Notice();
        BeanUtils.copyProperties(noticeDTO, notice);
        String msg = noticeService.updateShopNotice(notice, shopId);
        return Result.success(msg);
    }

    /**
     * 商家删除自己店铺公告
     */
    @DeleteMapping("/shop/delete/{id}")
    public Result<String> shopDelete(@PathVariable Long id, HttpServletRequest request) {
        Integer role = (Integer) request.getAttribute("loginRole");
        if (!RoleCheckUtil.isShop(role)) {
            return Result.error("无权限访问");
        }
        Long shopId = getLoginShopId(request);
        String msg = noticeService.deleteShopNotice(id, shopId);
        return Result.success(msg);
    }

    private List<NoticeVO> convertToVOList(List<Notice> noticeList) {
        return noticeList.stream().map(notice -> {
            NoticeVO noticeVO = new NoticeVO();
            BeanUtils.copyProperties(notice, noticeVO);
            return noticeVO;
        }).collect(Collectors.toList());
    }

    private Long getLoginShopId(HttpServletRequest request) {
        Long shopId = (Long) request.getAttribute("loginShopId");
        if (shopId == null) {
            shopId = (Long) request.getAttribute("loginId");
        }
        return shopId;
    }
}
