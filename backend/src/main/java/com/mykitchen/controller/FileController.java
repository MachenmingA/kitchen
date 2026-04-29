package com.mykitchen.controller;

import com.mykitchen.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
@Tag(name = "文件上传", description = "图片文件上传接口")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/image")
    @Operation(summary = "上传图片", description = "支持 jpg、png、gif、webp 格式，最大 5MB")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的图片");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只能上传图片文件");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error("图片大小不能超过 5MB");
        }

        try {
            String url = fileService.uploadImage(file);
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
