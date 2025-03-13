package com.scheduler.controller;

import com.scheduler.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/files")
public class FileController {
    
    @Value("${app.upload.dir:${user.home}/scheduler/uploads}")
    private String uploadDir;
    
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 确保上传目录存在
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;
            
            // 保存文件
            Path filePath = Paths.get(uploadDir, filename);
            file.transferTo(filePath.toFile());
            
            // 返回文件路径
            return Result.success(filePath.toString());
        } catch (Exception e) {
            log.error("File upload failed", e);
            return Result.error(500, "File upload failed: " + e.getMessage());
        }
    }
}
