package com.backend.directory.dto.response;

public record CreateDirectoryResponse(
        Long directoryId,
        String directoryName
) {
}
