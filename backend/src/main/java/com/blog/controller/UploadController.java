package com.blog.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
public class UploadController {

    private static final Set<String> AUDIO_EXT = Set.of("mp3", "wav", "m4a", "flac", "ogg");
    private static final Set<String> IMAGE_EXT = Set.of("jpg", "jpeg", "png", "webp", "gif");

    @Value("${blog.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping("/api/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file,
                                    @RequestParam(value = "type", defaultValue = "music") String type) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "empty file"));
        }
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            ext = original.substring(dot + 1).toLowerCase(Locale.ROOT);
        }
        if ("music".equals(type) && !AUDIO_EXT.contains(ext)) {
            return ResponseEntity.badRequest().body(Map.of("error", "仅支持音频文件: " + AUDIO_EXT));
        }
        if ("image".equals(type) && !IMAGE_EXT.contains(ext)) {
            return ResponseEntity.badRequest().body(Map.of("error", "仅支持图片: " + IMAGE_EXT));
        }
        try {
            File dir = new File(uploadDir, type);
            if (!dir.exists() && !dir.mkdirs()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "无法创建目录"));
            }
            String name = UUID.randomUUID().toString().replace("-", "") + "." + ext;
            File target = new File(dir, name);
            file.transferTo(target.toPath());
            String url = "/files/" + type + "/" + name;
            return ResponseEntity.ok(Map.of("url", url, "size", file.getSize()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "上传失败: " + e.getMessage()));
        }
    }
}
