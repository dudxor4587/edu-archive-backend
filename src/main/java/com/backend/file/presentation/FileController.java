package com.backend.file.presentation;

import com.backend.file.exception.FileNotFoundException;
import com.backend.file.service.FileService;
import com.backend.file.util.FileDownloadUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final FileDownloadUtil fileDownloadUtil;

    @HasRole({"ADMIN", "MANAGER"})
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("directoryId") Long directoryId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        try {
            fileService.uploadFile(file, directoryId, userId);
        } catch (Exception e) {
            log.error("파일 업로드 중 오류 발생", e);
            return ResponseEntity.badRequest().body("파일 업로드에 실패했습니다.");
        }
        return ResponseEntity.ok("파일이 업로드되었습니다.");
    }

    @HasRole({"ADMIN", "MANAGER", "MEMBER"})
    @GetMapping("/download")
    public void downloadFile(HttpServletResponse response, @RequestParam("url") String url) {
        File file;
        try {
            file = fileService.getFile(url);
            fileDownloadUtil.writeFileToResponse(file, response);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (IOException e) {
            log.error("파일 다운로드 중 오류 발생", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
