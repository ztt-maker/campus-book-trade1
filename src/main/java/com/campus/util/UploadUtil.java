package com.campus.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传工具类
 * 使用 commons-fileupload 解析 multipart 表单：
 * 1. 普通字段原样放入 Map
 * 2. 图片保存到项目 images 目录，Map 中保存“images/xxx.jpg”这样的相对路径
 */
public class UploadUtil {

    /** 图片保存目录（相对于 Web 应用根目录，即 webapp/images） */
    public static final String IMAGE_DIR = "images";

    /** 允许的图片后缀 */
    private static final String ALLOWED_EXT = "jpg|jpeg|png|gif|bmp|webp";

    /** 单个图片大小上限：5MB */
    private static final long MAX_SIZE = 5 * 1024 * 1024;

    /**
     * 解析 multipart 表单
     *
     * @return key 为表单字段名，value 为字段值或图片相对路径
     */
    public static Map<String, String> parse(HttpServletRequest request) throws Exception {
        Map<String, String> data = new HashMap<>();

        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");   // 解决中文乱码
        upload.setFileSizeMax(MAX_SIZE);     // 超过会抛 FileSizeLimitExceededException

        List<FileItem> items = upload.parseRequest(request);
        for (FileItem item : items) {
            if (item.isFormField()) {
                // ---------- 普通表单字段 ----------
                data.put(item.getFieldName(), item.getString("UTF-8"));
            } else {
                // ---------- 图片文件 ----------
                String fileName = item.getName();
                if (fileName == null || fileName.trim().isEmpty()) {
                    continue; // 没有选择文件，跳过
                }

                // 取后缀名并校验
                String ext = "";
                int dot = fileName.lastIndexOf('.');
                if (dot >= 0) {
                    ext = fileName.substring(dot).toLowerCase();
                }
                if (!ext.matches("\\.(" + ALLOWED_EXT + ")")) {
                    throw new IllegalArgumentException("只支持 jpg、jpeg、png、gif、bmp、webp 格式的图片");
                }

                // 用“时间戳 + 随机串”重命名，避免中文名和重名问题
                String stamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                String random = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
                String newName = stamp + "_" + random + ext;

                // 找到项目 images 目录（部署后的真实路径）
                String realDir = request.getServletContext().getRealPath("/" + IMAGE_DIR);
                if (realDir == null) {
                    throw new IllegalStateException("无法定位图片保存目录，请检查 Tomcat 部署方式");
                }
                File dir = new File(realDir);
                if (!dir.exists() && !dir.mkdirs()) {
                    throw new IllegalStateException("图片保存目录创建失败：" + realDir);
                }

                // 保存文件
                item.write(new File(dir, newName));

                // 数据库只存相对路径，如 images/20261030213000_ab12cd34.jpg
                data.put(item.getFieldName(), IMAGE_DIR + "/" + newName);
            }
        }
        return data;
    }
}
