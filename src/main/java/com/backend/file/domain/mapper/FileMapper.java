package com.backend.file.domain.mapper;



import com.backend.file.domain.File;
import com.backend.file.dto.response.FileResponse;
import java.util.List;
import java.util.stream.Collectors;

public class FileMapper {
    public static List<FileResponse> toFileResponse(List<File> files) {
        return files.stream()
                .map(file -> new FileResponse(
                        file.getName(),
                        file.getUrl()
                ))
                .collect(Collectors.toList());
    }
}
