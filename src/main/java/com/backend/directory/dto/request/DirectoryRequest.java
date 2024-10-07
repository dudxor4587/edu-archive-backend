package com.backend.directory.dto.request;

import org.antlr.v4.runtime.misc.NotNull;

public record DirectoryRequest(
        @NotNull
        String directoryName,
        Long parentId
) {
}
