package com.backend.file.dto.response;

public record FileResponse(
        Long fileId,
        String fileName,
        String url
) {
}
