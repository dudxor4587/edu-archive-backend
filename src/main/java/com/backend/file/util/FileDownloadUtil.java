package com.backend.file.util;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FileDownloadUtil {

    public void writeFileToResponse(File file, HttpServletResponse response, Long userId) throws IOException {
        response.setContentType("application/octet-stream");
        String encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);
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
        }
        log.info("user {} downloaded file {}", userId, file.getName());
    }
}
