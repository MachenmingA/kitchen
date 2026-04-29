package com.mykitchen.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceTest {

    private FileService fileService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileService = new FileService();
        // 通过反射设置私有字段
        try {
            java.lang.reflect.Field uploadPathField = FileService.class.getDeclaredField("uploadPath");
            uploadPathField.setAccessible(true);
            uploadPathField.set(fileService, tempDir.toString());

            java.lang.reflect.Field baseUrlField = FileService.class.getDeclaredField("baseUrl");
            baseUrlField.setAccessible(true);
            baseUrlField.set(fileService, "http://localhost:8080");
        } catch (Exception e) {
            fail("Failed to set up FileService: " + e.getMessage());
        }
    }

    @Test
    void uploadImage_ShouldSaveFileAndReturnUrl() throws IOException {
        byte[] content = "test image content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test-image.jpg",
            "image/jpeg",
            content
        );

        String result = fileService.uploadImage(file);

        assertNotNull(result);
        assertTrue(result.startsWith("http://localhost:8080/uploads/"));
        assertTrue(result.endsWith(".jpg"));

        // 验证文件确实被保存
        String relativePath = result.replace("http://localhost:8080/uploads/", "");
        Path savedFile = tempDir.resolve(relativePath);
        assertTrue(Files.exists(savedFile));
        assertEquals(content.length, Files.size(savedFile));
    }

    @Test
    void uploadImage_ShouldCreateDateBasedDirectories() throws IOException {
        byte[] content = "test image".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.png",
            "image/png",
            content
        );

        String result = fileService.uploadImage(file);

        assertNotNull(result);

        // 验证日期目录存在
        java.time.LocalDate now = java.time.LocalDate.now();
        String datePath = String.format("%d/%02d/%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());
        String relativePath = result.replace("http://localhost:8080/uploads/", "");
        assertTrue(relativePath.startsWith(datePath));
    }

    @Test
    void uploadImage_ShouldGenerateUniqueFilename() throws IOException {
        byte[] content = "test image".getBytes();

        MockMultipartFile file1 = new MockMultipartFile(
            "file", "same-name.jpg", "image/jpeg", content
        );
        MockMultipartFile file2 = new MockMultipartFile(
            "file", "same-name.jpg", "image/jpeg", content
        );

        String url1 = fileService.uploadImage(file1);
        String url2 = fileService.uploadImage(file2);

        assertNotEquals(url1, url2);
    }

    @Test
    void deleteImage_ShouldDeleteExistingFile() throws IOException {
        // 先上传一个文件
        byte[] content = "test image".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "to-delete.jpg",
            "image/jpeg",
            content
        );
        String url = fileService.uploadImage(file);

        // 验证文件存在
        String relativePath = url.replace("http://localhost:8080/uploads/", "");
        Path savedFile = tempDir.resolve(relativePath);
        assertTrue(Files.exists(savedFile));

        // 删除文件
        fileService.deleteImage(url);

        // 验证文件已被删除
        assertFalse(Files.exists(savedFile));
    }

    @Test
    void deleteImage_ShouldHandleNullUrl() {
        assertDoesNotThrow(() -> fileService.deleteImage(null));
    }

    @Test
    void deleteImage_ShouldHandleEmptyUrl() {
        assertDoesNotThrow(() -> fileService.deleteImage(""));
    }

    @Test
    void uploadImage_ShouldSupportDifferentExtensions() throws IOException {
        String[] extensions = {".jpg", ".png", ".gif", ".webp"};

        for (String ext : extensions) {
            String filename = "test" + ext;
            MockMultipartFile file = new MockMultipartFile(
                "file",
                filename,
                "image/jpeg",
                "content".getBytes()
            );

            String result = fileService.uploadImage(file);

            assertNotNull(result);
            assertTrue(result.endsWith(ext));
        }
    }
}
