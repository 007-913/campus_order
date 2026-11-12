package org.example.campus_order.controller;

import org.example.campus_order.utils.FileUploadUtil;
import org.example.campus_order.utils.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

    /** 允许的文件类型 */
    private static final String[] ALLOWED_TYPES = {
            "image/jpeg", "image/png", "image/gif", "image/webp"
    };

    /** 最大文件大小 5MB */
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    @PostMapping
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        // 校验文件非空
        if (file == null || file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        // 校验文件大小
        if (file.getSize() > MAX_SIZE) {
            return Result.error("文件大小不能超过5MB");
        }
        // 校验文件类型
        String contentType = file.getContentType();
        if (contentType == null || !isAllowedType(contentType)) {
            return Result.error("仅支持上传 JPG/PNG/GIF/WEBP 格式图片");
        }

        try {
            String path = FileUploadUtil.upload(file);
            return Result.success(path);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    private boolean isAllowedType(String contentType) {
        for (String allowed : ALLOWED_TYPES) {
            if (allowed.equals(contentType)) return true;
        }
        return false;
    }
}
