package com.backend.directory.dto.request;

import jakarta.validation.constraints.NotNull;

public record DirectoryCreateRequest(
        Long parentId,
        @NotNull(message = "디렉토리 이름은 필수입니다.")
        String directoryName
) {
}
