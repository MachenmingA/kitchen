package com.mykitchen.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

    @Value("${app.upload.path:uploads}")
    private String uploadPath;

    @Value("${app.upload.base-url:http://localhost:8080}")
    private String baseUrl;

    public String uploadImage(MultipartFile file) throws IOException {
        // 创建上传目录
        Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;

        // 按日期分类存储
        java.time.LocalDate now = java.time.LocalDate.now();
        String datePath = String.format("%d/%02d/%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        Path targetDir = uploadDir.resolve(datePath);
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // 保存文件
        Path targetPath = targetDir.resolve(newFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // 返回访问URL
        String relativePath = datePath + "/" + newFilename;
        return baseUrl + "/uploads/" + relativePath;
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            // 从URL中提取相对路径
            String relativePath = imageUrl.replace(baseUrl + "/uploads/", "");
            Path filePath = Paths.get(uploadPath, relativePath).toAbsolutePath().normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("删除图片失败: {}", imageUrl, e);
        }
    }
}
