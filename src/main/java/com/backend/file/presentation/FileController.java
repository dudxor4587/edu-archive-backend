package com.backend.file.presentation;

import com.backend.file.service.FileService;
import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("directoryId") Long directoryId) {
        try {
            fileService.uploadFile(file, directoryId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("파일 업로드에 실패했습니다.");
        }
        return ResponseEntity.ok("파일이 업로드되었습니다.");
    }

    @PostMapping("/download")
    public ResponseEntity<FileSystemResource> downloadFile(@RequestParam("url") String url) {
        String filePath = "src/main/resources/files/" + url;
        File file = new File(filePath);

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        FileSystemResource resource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}

