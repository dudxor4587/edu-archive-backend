package com.backend.directory.dto.response;

import com.backend.file.dto.response.FileResponse;
import java.util.List;

public record DirectoryResponse(
        String directoryName,
        Long directoryId,
        Long parentId,
        List<FileResponse> files,
        List<DirectoryResponse> directories
) {
}
