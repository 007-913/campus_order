package org.example.campus_order.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

public class FileUploadUtil {

    /** 默认上传目录，可通过 -Dupload.path 覆盖 */
    private static final String DEFAULT_DIR = "D:/campus-upload/";

    private static final long MAX_FILENAME_LENGTH = 128;

    public static String upload(MultipartFile file) throws Exception {
        // 从系统属性读取上传路径，优先级: 系统属性 > 默认值
        String dir = System.getProperty("upload.path", DEFAULT_DIR);
        if (!dir.endsWith("/")) {
            dir += "/";
        }

        File folder = new File(dir);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        // 防止路径穿越: 去掉路径分隔符
        originalFilename = originalFilename.replace("..", "").replace("/", "").replace("\\", "");

        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID() + suffix;

        // 防止文件名过长
        if (filename.length() > MAX_FILENAME_LENGTH) {
            filename = UUID.randomUUID().toString().substring(0, 8) + suffix;
        }

        file.transferTo(new File(dir + filename));

        return "/upload/" + filename;
    }
}
