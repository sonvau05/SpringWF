package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class FileController {

    private final String UPLOAD_DIR = "uploads/";

    // Banned extensions for security
    private final List<String> BANNED_EXTENSIONS = Arrays.asList(
            "exe", "bat", "sh", "cmd", "msi", "vbs", "js", "jar", "scr", "pif"
    );

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty.");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            fileExtension = originalFilename.substring(dotIndex + 1).toLowerCase();
        }

        // Check if the file is an executable or banned type
        if (BANNED_EXTENSIONS.contains(fileExtension)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Không được phép tải lên định dạng file này (tệp thực thi) để đảm bảo an toàn.");
        }

        try {
            // Create uploads directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique file name to avoid collisions
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Construct file URL
            String fileUrl = "/uploads/" + uniqueFileName;

            Map<String, String> response = new HashMap<>();
            response.put("fileName", originalFilename);
            response.put("fileUrl", fileUrl);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        }
    }
}
