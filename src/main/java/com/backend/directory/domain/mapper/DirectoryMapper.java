package com.backend.directory.domain.mapper;

import static com.backend.file.domain.mapper.FileMapper.toFileResponse;

import com.backend.directory.dto.response.DirectoryResponse;
import com.backend.directory.domain.Directory;

import java.util.List;
import java.util.stream.Collectors;

public class DirectoryMapper {

    public static List<DirectoryResponse> toDirectoryResponse(List<Directory> directories) {
        return directories.stream()
                .filter(directory -> directory.getParent() == null) // 루트 디렉토리만 필터링
                .map(rootDirectory -> new DirectoryResponse(
                        rootDirectory.getName(),
                        rootDirectory.getDirectoryId(),
                        null, // 루트 디렉토리의 parentId는 null
                        toFileResponse(rootDirectory.getFiles()),
                        findChildren(rootDirectory) // 자식 디렉토리 찾기
                ))
                .collect(Collectors.toList());
    }

    private static List<DirectoryResponse> findChildren(Directory parent) {
        return parent.getChildren().stream() // 부모의 children 속성을 사용
                .map(directory -> new DirectoryResponse(
                        directory.getName(),
                        directory.getDirectoryId(),
                        directory.getParent() != null ? directory.getParent().getDirectoryId() : null,
                        toFileResponse(directory.getFiles()),
                        findChildren(directory) // 자식의 자식 찾기
                ))
                .collect(Collectors.toList());
    }
}
