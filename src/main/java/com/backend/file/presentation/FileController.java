package com.backend.file.presentation;

import com.backend.file.service.FileService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.backend.auth.HasRole;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @HasRole({"ADMIN", "MANAGER"})
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("directoryId") Long directoryId) {
        try {
            fileService.uploadFile(file, directoryId);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("파일 업로드에 실패했습니다.");
        }
        return ResponseEntity.ok("파일이 업로드되었습니다.");
    }

    @HasRole({"ADMIN", "MANAGER", "MEMBER"})
    @GetMapping("/download")
    public void downloadFile(HttpServletResponse response, @RequestParam("url") String url) {
        String filePath = "/home/ubuntu/EduArchive/edu-archive-backend/files/" + url;
        File file = new File(filePath);

        if (!file.exists()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType("application/octet-stream");

        // UTF-8 인코딩된 파일 이름 처리
        String encodedFileName;
        encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName);

        response.setContentLength((int) file.length());

        try (ServletOutputStream out = response.getOutputStream();
            FileInputStream in = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                out.flush();
            }
        } catch (IOException e) {
            log.error("파일 다운로드 중 오류 발생", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
