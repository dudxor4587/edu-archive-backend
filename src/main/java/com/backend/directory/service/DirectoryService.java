package com.backend.directory.service;

import com.backend.directory.domain.Directory;
import com.backend.directory.domain.mapper.DirectoryMapper;
import com.backend.directory.domain.repository.DirectoryRepository;
import com.backend.directory.dto.response.CreateDirectoryResponse;
import com.backend.directory.dto.response.DirectoryResponse;
import com.backend.directory.exception.DirectoryNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DirectoryService {
    private final DirectoryRepository directoryRepository;

    @Transactional
    public CreateDirectoryResponse createDirectory(Long parentId, String directoryName) {
        if (parentId == null) {
            Directory newDirectory = directoryRepository.save(Directory.builder()
                    .name(directoryName)
                    .build());

            return new CreateDirectoryResponse(newDirectory.getDirectoryId(), newDirectory.getName());
        }

        Directory parent = directoryRepository.findById(parentId)
                .orElseThrow(() -> new DirectoryNotFoundException("부모 디렉토리를 찾을 수 없습니다."));

        Directory newDirectory = directoryRepository.save(Directory.builder()
                .name(directoryName)
                .parent(parent)
                .build());

        return new CreateDirectoryResponse(newDirectory.getDirectoryId(), newDirectory.getName());
    }

    @Transactional(readOnly = true)
    public List<DirectoryResponse> getDirectories() {
        List<Directory> directories = directoryRepository.findAllDirectories();
        return DirectoryMapper.toDirectoryResponse(directories);
    }
}
